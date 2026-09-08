# Java AI Projects

A collection of Artificial Intelligence assignments. All projects are written in pure Java and focus on classification using the Wisconsin Breast Cancer dataset.

## Projects

| Project | Description | Key Techniques |
|---------|-------------|----------------|
| [Genetic Programming](./ai-genetic-programming/) | Evolves arithmetic expression trees to classify breast cancer | GP, Symbolic Regression, Parsimony Pressure |
| [Decision Tree](./ai-decision-tree/) | Builds a decision tree with weighted gain ratio | Gain Ratio, Entropy, Feature Subspace |

## Common Dataset

Both projects use the Wisconsin Breast Cancer dataset:
- `Breast_train.csv` – training data (70% of samples)
- `Breast_test.csv` – testing data (30% of samples)

The data has 9 features and a binary class label (0 = benign, 1 = malignant).

## How to Run Any Project

1. Navigate to the project folder:
   ```bash
   cd ai-genetic-programming   # or ai-decision-tree
Compile the Java source:

bash
javac *.java
Run the main class (check the sub-README for details):

bash
java MainClassName
Each project folder contains its own README.md with detailed instructions.

License

MIT – free to use for learning or reference.

text

5. Scroll all the way down and tap the green **"Commit new file"** button.

---

## Step 3: Create the .gitignore File

1. Tap **"Add file"** again → **"Create new file"**.
2. In the filename box, type exactly:
.gitignore

text
*(Note: it starts with a dot)*
3. In the text box, paste this:

```gitignore
# Compiled Java bytecode
*.class

# Logs and generated data
*.log

# OS junk
.DS_Store
Thumbs.db

# IDE / editor files
.idea/
*.iml
.vscode/
.settings/
.project
.classpath
