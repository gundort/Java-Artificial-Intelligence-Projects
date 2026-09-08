AI – Genetic Programming (ai-genetic-programming/README.md)

markdown
# Arithmetic Genetic Programming (Breast Cancer Classification)

A symbolic regression system using Genetic Programming (GP). The program evolves arithmetic expression trees to classify breast cancer cases based on the Wisconsin Breast Cancer dataset. It uses a population of expression trees, applies crossover, mutation, and parsimony pressure, and tunes the classification threshold to maximise F-measure.

## Features

- Ramped half-and-half initialisation – creates diverse trees of varying depths.
- Tournament selection – selects parents based on fitness (accuracy minus parsimony penalty).
- Subtree crossover and point mutation – standard GP operators.
- Parsimony pressure – penalises large trees to control bloat.
- Dynamic threshold tuning – finds the optimal classification threshold on training data to maximise F-measure.
- F-measure evaluation – reports precision, recall, and F1 on test data.
- Multiple runs – allows averaging results over several seeds.
- Configurable parameters – population size, generations, rates, depth limit, etc.

## Technologies

- Java (JDK 8+)
- Plain text CSV – reads training and test data.
- Tree-based representation – arithmetic expressions with protected division.

## Prerequisites

- Java Runtime Environment (JRE) or JDK installed.
- The dataset CSV files (training and testing) with the same format as the provided `Breast_train.csv` and `Breast_test.csv`.

## Setup & Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/java-ai-pracs.git
   cd java-ai-pracs/ai-genetic-programming
Compile the Java source:

bash
javac ArithmeticGP.java
Ensure your CSV files are in the same directory (or provide full paths when prompted).
Usage

Run the program and follow the prompts:

bash
java ArithmeticGP
You will be asked for:

Training CSV file path – e.g., Breast_train.csv
Testing CSV file path – e.g., Breast_test.csv
Random seed – an integer (use the same seed for reproducibility)
Number of runs – 1 for a quick demo, 30 for a full experiment.
Example session:

text
Enter training CSV file path: Breast_train.csv
Enter testing CSV file path: Breast_test.csv
Enter random seed (integer): 42
Number of runs (1 for demo, 30 for full): 1
The program will output the best individual from each generation, and finally the test accuracy and F-measure for the best tree. If you run multiple runs, it will summarise the best test accuracy across all runs.

Sample output:

text
Gen   0: best acc=0.7125, tree size=15, tree=((x1 + x3) / (x2 * x4))
...
Result: train acc=0.9125, test acc=0.8750, F=0.8621, size=23, time=4.52 sec
Best tree: ((x1 + x3) / (x2 * x4)) + ...
File Structure

text
ai-genetic-programming/
├── ArithmeticGP.java      # Main GP program
├── Breast_train.csv       # Example training data
├── Breast_test.csv        # Example testing data
└── README.md              # This file
Notes / Caveats

The GP uses protected division – if the denominator is zero, it returns 1.0 to avoid runtime errors.
The depth limit is enforced after mutation/crossover to keep trees within MAX_DEPTH.
The parsimony coefficient (PARSIMONY_COEFF = 0.0001) is tiny, so fitness is dominated by accuracy.
Threshold tuning is performed on the training set using a coarse grid search (50 steps) – this is efficient and works well in practice.
The program does not normalise features; the data is already encoded as integers.
The program is single-threaded; for 100 generations and population 200, it may take a few seconds per run.
License

MIT – free to use for learning or reference.
