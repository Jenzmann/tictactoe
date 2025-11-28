import pandas as pd
import matplotlib.pyplot as plt
import seaborn as sns

try:
    df_train = pd.read_csv('Trainingsdaten.csv', sep=';')
except:
    df_train = pd.read_csv('Trainingsdaten.csv', sep=',')

# a) Analyse des Zielattributs
print(df_train['TARGET_BETRUG'].value_counts())
print(df_train['TARGET_BETRUG'].value_counts(normalize=True) * 100)

# Plot
# sns.countplot(x='TARGET_BETRUG', data=df_train)
# plt.title('Verteilung Betrug vs. Kein Betrug')
# plt.show()



from sklearn.model_selection import train_test_split
from sklearn.preprocessing import LabelEncoder

# 1. Datenbereinigung und Vorverarbeitung
data = df_train.copy()
data['TARGET_BETRUG'] = data['TARGET_BETRUG'].map({'ja': 1, 'nein': 0})
cols_to_drop = ['BESTELLIDENT', 'ANUMMER_01', 'ANUMMER_02', 'ANUMMER_03',
                'ANUMMER_04', 'ANUMMER_05', 'ANUMMER_06', 'ANUMMER_07',
                'ANUMMER_08', 'ANUMMER_09', 'ANUMMER_10', 'B_GEBDATUM', 'DATUM_LBEST']
data_clean = data.drop(columns=cols_to_drop, errors='ignore')

le = LabelEncoder()
categorical_cols = data_clean.select_dtypes(include=['object']).columns

for col in categorical_cols:
    data_clean[col] = data_clean[col].astype(str)
    data_clean[col] = le.fit_transform(data_clean[col])

# fill null values; do not delete
data_clean = data_clean.fillna(0)

# Features (X) und Target (y) trennen
X = data_clean.drop('TARGET_BETRUG', axis=1)
y = data_clean['TARGET_BETRUG']

# 80% Training, 20% Test
X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42, stratify=y)
# stratify=y ist wichtig bei ungleichen Klassen, damit das Verhältnis in Train/Test gleich bleibt


from sklearn.tree import DecisionTreeClassifier
from sklearn.naive_bayes import GaussianNB
from sklearn.linear_model import LogisticRegression
from sklearn.metrics import confusion_matrix, accuracy_score, precision_score, recall_score, classification_report

# 3 modelle
models = {
    "Decision Tree": DecisionTreeClassifier(random_state=42),
    "Naive Bayes": GaussianNB(),
    "Logistic Regression": LogisticRegression(max_iter=1000, random_state=42)
}

results = {}

for name, model in models.items():
    model.fit(X_train, y_train)

    y_pred = model.predict(X_test)

    acc = accuracy_score(y_test, y_pred)
    prec = precision_score(y_test, y_pred, zero_division=0)
    rec = recall_score(y_test, y_pred)
    cm = confusion_matrix(y_test, y_pred)

    results[name] = {"Accuracy": acc, "Precision": prec, "Recall": rec, "CM": cm}

    print(f"--- {name} ---")
    print(f"Konfusionsmatrix:\n{cm}")
    print(f"Accuracy: {acc:.4f}")
    print(f"Precision: {prec:.4f}")
    print(f"Recall: {rec:.4f}")
    print("\n")


# ----------------------------------
# verhältniss anpassen (Undersampling)
from sklearn.utils import resample
train_data = pd.concat([X_train, y_train], axis=1)

not_fraud = train_data[train_data.TARGET_BETRUG == 0]
fraud = train_data[train_data.TARGET_BETRUG == 1]

not_fraud_downsampled = resample(not_fraud,
                                 replace=False,    # ohne Zurücklegen
                                 n_samples=len(fraud), # Anzahl an Betrugsfällen anpassen
                                 random_state=42)

downsampled = pd.concat([not_fraud_downsampled, fraud])
X_train_down = downsampled.drop('TARGET_BETRUG', axis=1)
y_train_down = downsampled['TARGET_BETRUG']

print(f"Neue Trainingsgröße nach Undersampling: {len(X_train_down)}")

best_model = DecisionTreeClassifier(random_state=42, max_depth=5)
best_model.fit(X_train_down, y_train_down)

# Testen auf den ursprünglichen (nicht manipulierten!) Testdaten
y_pred_improved = best_model.predict(X_test)

print("--- Decision Tree nach Undersampling ---")
print(confusion_matrix(y_test, y_pred_improved))
print(classification_report(y_test, y_pred_improved))


# -----------------------------

# 1. Klassifizierungsdaten laden
try:
    df_new = pd.read_csv('Klassifizierungsdaten.csv', sep=';')
except:
    df_new = pd.read_csv('Klassifizierungsdaten.csv', sep=',')

# 2. Gleiche Vorverarbeitung anwenden wie oben!
df_new_clean = df_new.drop(columns=cols_to_drop, errors='ignore')

for col in categorical_cols:
    if col in df_new_clean.columns:
        df_new_clean[col] = df_new_clean[col].astype(str)
        df_new_clean[col] = le.fit_transform(df_new_clean[col])

df_new_clean = df_new_clean.fillna(0)

features_needed = X.columns
df_new_final = df_new_clean[features_needed]

predictions = best_model.predict(df_new_final)

# Ergebnis speichern
df_new['PROGNOSE_BETRUG'] = predictions
df_new['PROGNOSE_BETRUG'] = df_new['PROGNOSE_BETRUG'].map({1: 'ja', 0: 'nein'})

print("Vorhersage abgeschlossen. Beispiel:")
print(df_new[['BESTELLIDENT', 'PROGNOSE_BETRUG']].head())

# Export
# df_new.to_csv('Ergebnis_Klassifizierung.csv', index=False, sep=';')