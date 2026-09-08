import java.io.*;
import java.util.*;

class Node {
    int featureIndex = -1;
    double threshold;
    int label;
    boolean isLeaf = false;

    Node left;
    Node right;
}

public class DecisionTree {

    static List<int[]> trainData = new ArrayList<>();
    static List<int[]> testData = new ArrayList<>();

    // =====================================
    // PARAMETERS
    // =====================================
    static int maxDepth = 4;
    static int minSamplesSplit = 25; 
    static double MIN_GAIN_RATIO = 0.01;

    // Class weights to handle distribution shift
    static double weight0 = 1.0;
    static double weight1 = 1.0;

    // =====================================
    // SEED CONFIGURATION
    // =====================================
    static int seed = 1;
    static Random rand;

    // =====================================
    // LOAD DATA & CALCULATE CLASS WEIGHTS
    // =====================================
    static List<int[]> loadCSV(String path) throws Exception {
        List<int[]> data = new ArrayList<>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line = br.readLine(); // skip header

        while ((line = br.readLine()) != null) {
            if (line.trim().isEmpty()) continue;

            String[] parts = line.split(",");
            int[] row = new int[parts.length];

            for (int i = 0; i < parts.length; i++) {
                row[i] = Integer.parseInt(parts[i].trim());
            }
            data.add(row);
        }
        br.close();
        return data;
    }

    static void calculateClassWeights(List<int[]> data) {
        int c0 = 0, c1 = 0;
        for (int[] row : data) {
            if (row[row.length - 1] == 0) c0++;
            else c1++;
        }
        if (c0 > 0 && c1 > 0) {
            // Inverse frequency balancing
            double total = data.size();
            weight0 = total / (2.0 * c0);
            weight1 = total / (2.0 * c1);
        }
    }

    // =====================================
    // WEIGHTED ENTROPY
    // =====================================
    static double entropy(List<int[]> data) {
        if (data.isEmpty()) return 0;

        double w0 = 0, w1 = 0;
        for (int[] row : data) {
            if (row[row.length - 1] == 0) w0 += weight0;
            else w1 += weight1;
        }

        double totalWeight = w0 + w1;
        if (totalWeight == 0) return 0;

        double p0 = w0 / totalWeight;
        double p1 = w1 / totalWeight;

        double e = 0;
        if (p0 > 0) e -= p0 * (Math.log(p0) / Math.log(2));
        if (p1 > 0) e -= p1 * (Math.log(p1) / Math.log(2));

        return e;
    }

    // =====================================
    // WEIGHTED MAJORITY CLASS
    // =====================================
    static int majorityClass(List<int[]> data) {
        double w0 = 0, w1 = 0;
        for (int[] row : data) {
            if (row[row.length - 1] == 0) w0 += weight0;
            else w1 += weight1;
        }
        return (w1 >= w0) ? 1 : 0;
    }

    // =====================================
    // BUILD TREE
    // =====================================
    static Node buildTree(List<int[]> data, int depth) {
        Node node = new Node();

        if (data.isEmpty()) {
            node.isLeaf = true;
            node.label = 0;
            return node;
        }

        node.label = majorityClass(data);

        if (data.size() < minSamplesSplit || depth >= maxDepth) {
            node.isLeaf = true;
            return node;
        }

        boolean pure = true;
        int first = data.get(0)[data.get(0).length - 1];
        for (int[] row : data) {
            if (row[row.length - 1] != first) {
                pure = false;
                break;
            }
        }

        if (pure) {
            node.isLeaf = true;
            node.label = first;
            return node;
        }

        int numFeatures = data.get(0).length - 1;
        double baseEntropy = entropy(data);

        double bestGainRatio = -1;
        int bestFeature = -1;
        double bestThreshold = -1;

        List<int[]> bestLeft = null;
        List<int[]> bestRight = null;

        List<Integer> featureList = new ArrayList<>();
        for (int i = 0; i < numFeatures; i++) {
            featureList.add(i);
        }

        Collections.shuffle(featureList, rand);

        // Feature subspace size handling
        int featuresToEvaluate = (int) Math.sqrt(numFeatures);
        if (featuresToEvaluate < 1) featuresToEvaluate = 1;

        for (int i = 0; i < featuresToEvaluate; i++) {
            int f = featureList.get(i);

            Set<Integer> values = new TreeSet<>();
            for (int[] row : data) {
                values.add(row[f]);
            }

            List<Integer> sorted = new ArrayList<>(values);

            for (int j = 0; j < sorted.size() - 1; j++) {
                double v1 = sorted.get(j);
                double v2 = sorted.get(j + 1);

                if (v1 == v2) continue;

                double threshold = v1 + (v2 - v1) / 2.0;

                List<int[]> left = new ArrayList<>();
                List<int[]> right = new ArrayList<>();

                double leftWeight = 0;
                double rightWeight = 0;

                for (int[] row : data) {
                    double w = (row[row.length - 1] == 0) ? weight0 : weight1;
                    if (row[f] <= threshold) {
                        left.add(row);
                        leftWeight += w;
                    } else {
                        right.add(row);
                        rightWeight += w;
                    }
                }

                if (left.isEmpty() || right.isEmpty()) continue;

                double totalWeight = leftWeight + rightWeight;
                double wL = leftWeight / totalWeight;
                double wR = rightWeight / totalWeight;

                double newEntropy = (wL * entropy(left)) + (wR * entropy(right));
                double gain = baseEntropy - newEntropy;

                if (gain <= 0) continue;

                double splitInfo = -wL * (Math.log(wL) / Math.log(2))
                                   -wR * (Math.log(wR) / Math.log(2));

                if (splitInfo == 0) continue;

                double gainRatio = gain / splitInfo;

                if (gainRatio > bestGainRatio) {
                    bestGainRatio = gainRatio;
                    bestFeature = f;
                    bestThreshold = threshold;
                    bestLeft = left;
                    bestRight = right;
                } else if (Math.abs(gainRatio - bestGainRatio) < 1e-9 && bestFeature != -1) {
                    if (f < bestFeature) {
                        bestFeature = f;
                        bestThreshold = threshold;
                        bestLeft = left;
                        bestRight = right;
                    }
                }
            }
        }

        if (bestFeature == -1 || bestGainRatio < MIN_GAIN_RATIO) {
            node.isLeaf = true;
            return node;
        }

        node.featureIndex = bestFeature;
        node.threshold = bestThreshold;

        node.left = buildTree(bestLeft, depth + 1);
        node.right = buildTree(bestRight, depth + 1);

        return node;
    }

    // =====================================
    // PREDICT
    // =====================================
    static int predict(Node node, int[] row) {
        if (node.isLeaf) return node.label;

        if (row[node.featureIndex] <= node.threshold) {
            return (node.left != null) ? predict(node.left, row) : node.label;
        } else {
            return (node.right != null) ? predict(node.right, row) : node.label;
        }
    }

    // =====================================
    // EVALUATION
    // =====================================
    static void evaluate(Node root, List<int[]> data, String name) {
        int correct = 0;
        int TP = 0, FP = 0, FN = 0;

        for (int[] row : data) {
            int actual = row[row.length - 1];
            int pred = predict(root, row);

            if (actual == pred) correct++;
            if (pred == 1 && actual == 1) TP++;
            if (pred == 1 && actual == 0) FP++;
            if (pred == 0 && actual == 1) FN++;
        }

        double acc = (double) correct / data.size();
        double precision = (TP + FP == 0) ? 0 : (double) TP / (TP + FP);
        double recall = (TP + FN == 0) ? 0 : (double) TP / (TP + FN);
        double f = (precision + recall == 0) ? 0 : 2 * precision * recall / (precision + recall);

        System.out.println("\n=========================");
        System.out.println(name + " RESULTS");
        System.out.println("=========================");
        System.out.println("Accuracy  : " + acc);
        System.out.println("Precision : " + precision);
        System.out.println("Recall    : " + recall);
        System.out.println("F-Measure : " + f);
    }

    // =====================================
    // PRINT TREE
    // =====================================
    static void printTree(Node node, String indent) {
        if (node.isLeaf) {
            System.out.println(indent + "Class = " + node.label);
            return;
        }

        System.out.println(indent + "Feature " + node.featureIndex +
                " <= " + node.threshold +
                " (Fallback = " + node.label + ")");

        System.out.println(indent + "  True:");
        printTree(node.left, indent + "    ");

        System.out.println(indent + "  False:");
        printTree(node.right, indent + "    ");
    }

    // =====================================
    // MAIN
    // =====================================
    public static void main(String[] args) throws Exception {
        Scanner sc = new Scanner(System.in);

        System.out.println("Max Depth: " + maxDepth);
        System.out.println("Min Samples Split: " + minSamplesSplit);

        System.out.print("\nEnter seed (1–30): ");
        seed = Integer.parseInt(sc.nextLine());

        rand = new Random(seed);

        System.out.print("\nEnter training CSV: ");
        String trainPath = sc.nextLine();

        System.out.print("Enter testing CSV: ");
        String testPath = sc.nextLine();

        trainData = loadCSV(trainPath);
        testData = loadCSV(testPath);

        // Dynamically compute the class weight modifiers on load
        calculateClassWeights(trainData);

        Node root = buildTree(trainData, 0);

        System.out.println("\n=========================");
        System.out.println("BALANCED SEED-BASED DECISION TREE");
        System.out.println("=========================");

        printTree(root, "");

        evaluate(root, trainData, "TRAINING");
        evaluate(root, testData, "TESTING");

        sc.close();
    }
}