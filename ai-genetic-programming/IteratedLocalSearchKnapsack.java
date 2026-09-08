import java.io.*;
import java.util.*;

/**
 * Iterated Local Search for 0/1 Knapsack.
 */
public class IteratedLocalSearchKnapsack {

    static class Knapsack {
        int n;
        long capacity;
        long[] weights;
        long[] values;

        Knapsack(int n, long capacity, long[] weights, long[] values) {
            this.n = n;
            this.capacity = capacity;
            this.weights = weights;
            this.values = values;
        }
    }

    static class Result {
        long bestValue;
        long bestWeight;
        boolean[] bestSolution;
        double runtimeSeconds;

        Result(long bestValue, long bestWeight, boolean[] bestSolution, double runtimeSeconds) {
            this.bestValue = bestValue;
            this.bestWeight = bestWeight;
            this.bestSolution = bestSolution;
            this.runtimeSeconds = runtimeSeconds;
        }
    }

    private final Random rand;
    private final Knapsack instance;

    public IteratedLocalSearchKnapsack(Knapsack instance, long seed) {
        this.instance = instance;
        this.rand = new Random(seed);
    }

    // Utility
    private String solutionToString(boolean[] sol) {
        StringBuilder sb = new StringBuilder(sol.length);
        for (boolean b : sol) sb.append(b ? '1' : '0');
        return sb.toString();
    }

    private long[] evaluate(boolean[] sol) {
        long totalValue = 0;
        long totalWeight = 0;
        for (int i = 0; i < instance.n; i++) {
            if (sol[i]) {
                totalValue += instance.values[i];
                totalWeight += instance.weights[i];
            }
        }
        return new long[]{totalValue, totalWeight};
    }

    private boolean isFeasible(boolean[] sol) {
        long weight = 0;
        for (int i = 0; i < instance.n; i++) {
            if (sol[i]) {
                weight += instance.weights[i];
                if (weight > instance.capacity) return false;
            }
        }
        return true;
    }

    // Initial solution
    private boolean[] greedySolution() {
        Integer[] indices = new Integer[instance.n];
        for (int i = 0; i < instance.n; i++) indices[i] = i;

        Arrays.sort(indices, (a, b) -> {
            double ra = (double) instance.values[a] / (double) instance.weights[a];
            double rb = (double) instance.values[b] / (double) instance.weights[b];
            return Double.compare(rb, ra);
        });

        boolean[] sol = new boolean[instance.n];
        long totalWeight = 0;

        for (int idx : indices) {
            if (totalWeight + instance.weights[idx] <= instance.capacity) {
                sol[idx] = true;
                totalWeight += instance.weights[idx];
            }
        }
        return sol;
    }

    // Local search (steepest ascent, single bit flip neighborhood)
    private boolean[] localSearch(boolean[] start) {
        boolean[] best = start.clone();
        long[] bestEval = evaluate(best);
        long bestValue = bestEval[0];

        boolean improved = true;
        while (improved) {
            improved = false;
            int bestFlip = -1;
            long bestCandidateValue = bestValue;

            for (int i = 0; i < instance.n; i++) {
                boolean[] candidate = best.clone();
                candidate[i] = !candidate[i];

                if (isFeasible(candidate)) {
                    long val = evaluate(candidate)[0];
                    if (val > bestCandidateValue) {
                        bestCandidateValue = val;
                        bestFlip = i;
                    }
                }
            }

            if (bestFlip != -1 && bestCandidateValue > bestValue) {
                best[bestFlip] = !best[bestFlip];
                bestValue = bestCandidateValue;
                improved = true;
            }
        }

        return best;
    }

    // Perturbation and repair
    private boolean[] perturb(boolean[] solution, int strength) {
        boolean[] newSol = solution.clone();

        int flips = Math.max(1, strength);
        Set<Integer> chosen = new HashSet<>();
        while (chosen.size() < flips && chosen.size() < instance.n) {
            chosen.add(rand.nextInt(instance.n));
        }

        for (int idx : chosen) {
            newSol[idx] = !newSol[idx];
        }

        // Fix until feasible so remove random selected items
        while (!isFeasible(newSol)) {
            List<Integer> selected = new ArrayList<>();
            for (int i = 0; i < instance.n; i++) {
                if (newSol[i]) selected.add(i);
            }
            if (selected.isEmpty()) break;

            int removeIdx = selected.get(rand.nextInt(selected.size()));
            newSol[removeIdx] = false;
        }

        return newSol;
    }

    // Main ILS loop
    public Result runILS(int maxIterations, int perturbationStrength) {
        long startTime = System.nanoTime();

        boolean[] current = greedySolution();
        current = localSearch(current);

        long[] curEval = evaluate(current);
        long currentValue = curEval[0];

        boolean[] best = current.clone();
        long bestValue = currentValue;
        long bestWeight = curEval[1];

        for (int iter = 0; iter < maxIterations; iter++) {
            boolean[] perturbed = perturb(current, perturbationStrength);
            boolean[] candidate = localSearch(perturbed);
            long[] candEval = evaluate(candidate);
            long candidateValue = candEval[0];
            long candidateWeight = candEval[1];

            if (candidateValue > currentValue) {
                current = candidate;
                currentValue = candidateValue;

                if (candidateValue > bestValue) {
                    best = candidate.clone();
                    bestValue = candidateValue;
                    bestWeight = candidateWeight;
                }
            }
        }

        long endTime = System.nanoTime();
        double runtimeSeconds = (endTime - startTime) / 1e9;

        return new Result(bestValue, bestWeight, best, runtimeSeconds);
    }

    // File reading
    public static Knapsack readInstance(String filename) throws IOException {
        List<double[]> items = new ArrayList<>();
        boolean hasDecimal = false;

        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;

            // Read the first non-empty, non-comment line: n capacity
            do {
                line = br.readLine();
                if (line == null) throw new IOException("Empty file: " + filename);
                line = line.trim();
            } while (line.isEmpty() || line.startsWith("#"));

            String[] header = line.split("\\s+");
            if (header.length < 2) {
                throw new IOException("Invalid header in file: " + filename);
            }

            int n = Integer.parseInt(header[0]);
            double rawCapacity = Double.parseDouble(header[1]);

            for (int i = 0; i < n; i++) {
                do {
                    line = br.readLine();
                    if (line == null) {
                        throw new IOException("Unexpected end of file in: " + filename);
                    }
                    line = line.trim();
                } while (line.isEmpty() || line.startsWith("#"));

                String[] parts = line.split("\\s+");
                if (parts.length < 2) {
                    throw new IOException("Invalid item line in file: " + filename + " -> " + line);
                }

                double w = Double.parseDouble(parts[0]);
                double v = Double.parseDouble(parts[1]);

                if (parts[0].contains(".") || parts[1].contains(".")) {
                    hasDecimal = true;
                }

                items.add(new double[]{w, v});
            }

            long scaleFactor = hasDecimal ? 1_000_000L : 1L;

            long[] weights = new long[n];
            long[] values = new long[n];

            for (int i = 0; i < n; i++) {
                double w = items.get(i)[0];
                double v = items.get(i)[1];
                weights[i] = Math.round(w * scaleFactor);
                values[i] = Math.round(v * scaleFactor);
            }

            long capacity = Math.round(rawCapacity * scaleFactor);
            return new Knapsack(n, capacity, weights, values);
        }
    }

    private static String printableName(String filename) {
        return new File(filename).getName();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter seed: ");
        long seed = scanner.nextLong();

        System.out.print("Enter folder path containing problem files: ");
        String folderPath = scanner.next();

        File folder = new File(folderPath);
        File[] allFiles = folder.listFiles();

        if (allFiles == null || allFiles.length == 0) {
            System.out.println("No problem files found.");
            scanner.close();
            return;
        }

        List<File> fileList = new ArrayList<>();
        for (File f : allFiles) {
            if (f.isFile() && !f.getName().endsWith(".xlsx")) {
                fileList.add(f);
            }
        }

        fileList.sort(Comparator.comparing(File::getName));

        if (fileList.isEmpty()) {
            System.out.println("No valid knapsack files found.");
            scanner.close();
            return;
        }

        // Known optimums for reporting only
        Map<String, Long> knownOpt = new HashMap<>();
        knownOpt.put("f1_l-d_kp_10_269", 295L);
        knownOpt.put("f2_l-d_kp_20_878", 1024L);
        knownOpt.put("f3_l-d_kp_4_20", 35L);
        knownOpt.put("f4_l-d_kp_4_11", 23L);
        knownOpt.put("f5_l-d_kp_15_375", 481069400L); // scaled by 1,000,000
        knownOpt.put("f6_l-d_kp_10_60", 52L);
        knownOpt.put("f7_l-d_kp_7_50", 107L);
        knownOpt.put("knapPI_1_100_1000_1", 9147L);
        knownOpt.put("f8_l-d_kp_23_10000", 9767L);
        knownOpt.put("f9_l-d_kp_5_80", 130L);
        knownOpt.put("f10_l-d_kp_20_879", 1025L);

        int maxIterations = 50000;
        int perturbationStrength = 5;

        System.out.printf("%-25s %-18s %-18s %-22s %-12s%n",
                "Instance", "ILS_Best", "Known_Opt", "Best_Solution", "Runtime(s)");

        for (File f : fileList) {
            try {
                Knapsack inst = readInstance(f.getAbsolutePath());
                IteratedLocalSearchKnapsack ils = new IteratedLocalSearchKnapsack(inst, seed);

                Result result = ils.runILS(maxIterations, perturbationStrength);

                long optimum = knownOpt.getOrDefault(f.getName(), 0L);
                String bestSolStr = ils.solutionToString(result.bestSolution);

                System.out.printf("%-25s %-18d %-18d %-22s %-12.3f%n",
                        printableName(f.getName()),
                        result.bestValue,
                        optimum,
                        bestSolStr,
                        result.runtimeSeconds);

            } catch (Exception e) {
                System.err.println("Error processing " + f.getName() + ": " + e.getMessage());
            }
        }

        scanner.close();
    }
}