## AI – Decision Tree (`ai-decision-tree/README.md`)

```markdown
# Decision Tree Classifier (Breast Cancer)

A custom implementation of a decision tree classifier using the gain ratio splitting criterion and class-weighted entropy to handle class imbalance. The tree is built with a random feature subspace (a form of random forest inspiration) and includes pruning via depth and sample size limits.

## Features

- Gain ratio – chooses splits based on information gain normalised by split information.
- Weighted entropy – accounts for class imbalance using inverse-frequency weights.
- Feature subspace – evaluates only a random subset of features at each node (size = sqrt(total features)).
- Depth and sample limits – stops splitting to avoid overfitting.
- Seed-based reproducibility – ensures consistent results across runs.
- Evaluation – reports accuracy, precision, recall, and F-measure on both training and test sets.
- Tree printing – displays the learned tree in a human-readable format.

## Technologies

- Java (JDK 8+)
- Plain text CSV – reads training and test data.

## Prerequisites

- Java Runtime Environment (JRE) or JDK installed.
- The dataset CSV files with the same format as provided.

## Setup & Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/java-ai-pracs.git
   cd java-ai-pracs/ai-decision-tree
Compile the Java source:

bash
javac DecisionTree.java
Ensure your CSV files are in the same directory (or provide full paths when prompted).
Usage

Run the program and follow the prompts:

bash
java DecisionTree
You will be asked for:

Seed – an integer (1–30 recommended) for reproducibility.
Training CSV file path – e.g., Breast_train.csv
Testing CSV file path – e.g., Breast_test.csv
Example session:

text
Max Depth: 4
Min Samples Split: 25

Enter seed (1–30): 1

Enter training CSV: Breast_train.csv
Enter testing CSV: Breast_test.csv
The program will:

Load the data.
Compute class weights to balance the training set.
Build the decision tree using gain ratio and feature subspace.
Print the tree structure.
Evaluate on both training and test sets.
Sample output:

text
=========================
BALANCED SEED-BASED DECISION TREE
=========================
Feature 5 <= 2.0 (Fallback = 0)
  True:
    Feature 3 <= 1.5 (Fallback = 0)
      True:
        Class = 0
      False:
        Class = 1
  False:
    Feature 4 <= 1.0 (Fallback = 1)
      True:
        Class = 0
      False:
        Class = 1

=========================
TRAINING RESULTS
=========================
Accuracy  : 0.9234
Precision : 0.9123
Recall    : 0.9012
F-Measure : 0.9067

TESTING RESULTS
=========================
Accuracy  : 0.8876
...
File Structure

text
ai-decision-tree/
├── DecisionTree.java       # Main decision tree implementation
├── Breast_train.csv        # Example training data
├── Breast_test.csv         # Example testing data
└── README.md               # This file
Notes / Caveats

The tree uses gain ratio to select the best split, which avoids bias towards multi-valued features.
Class weights are computed as inverse frequencies to balance the training set.
The tree stops splitting when the node is pure, depth reaches maxDepth, or the sample size falls below minSamplesSplit.
The feature subspace size is sqrt(numFeatures) – a common random forest trick to increase diversity.
The tree is not pruned after building; early stopping is used instead.
The default minSamplesSplit is 25, which may be high for a small dataset; this is configurable in the source code.
License

MIT – free to use for learning or reference.

text

---

## Summary of What to Copy

| Folder | File to Create | Content to Paste |
|--------|----------------|------------------|
| `ai-genetic-programming/` | `README.md` | First README (Genetic Programming) |
| `ai-decision-tree/` | `README.md` | Second README (Decision Tree) |

---

## Quick Guide – How to Create These on GitHub

1. Go to your AI repo: `https://github.com/YOUR_USERNAME/Java-AI-Pracs`
2. Tap the folder you want to add the README to (e.g., `ai-genetic-programming`)
3. Tap **"Add file"** → **"Create new file"**
4. In the filename box, type `README.md`
5. Paste the corresponding README content
6. Tap **"Commit new file"**

Repeat for the second project.
