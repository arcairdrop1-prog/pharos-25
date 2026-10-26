import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TrendWatcherAgent {

    public static class Observation {
        LocalDateTime timestamp;
        double value;

        public Observation(LocalDateTime timestamp, double value) {
            this.timestamp = timestamp;
            this.value = value;
        }

        public String serialize() {
            return timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) + "," + value;
        }

        public static Observation deserialize(String line) {
            String[] parts = line.split(",");
            return new Observation(LocalDateTime.parse(parts[0]), Double.parseDouble(parts[1]));
        }

        @Override
        public String toString() {
            return String.format("[%s] Valor: %.2f", timestamp.format(DateTimeFormatter.ofPattern("HH:mm:ss")), value);
        }
    }

    private final String storageFilePath = "trend_state.csv";
    private final List<Observation> history = new ArrayList<>();
    private final int requiredConsecutiveObservations = 3; // Requisito 02: Pelo menos 3 observações

    public TrendWatcherAgent() {
        loadState(); // Requisito 01 & 03: Carrega o estado persistente e lida com reinícios sem perder histórico
    }

    // Requisito 01 & 03: Armazena o estado em ficheiro persistente
    private void saveState() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(storageFilePath))) {
            for (Observation obs : history) {
                writer.println(obs.serialize());
            }
        } catch (IOException e) {
            System.out.println("Erro ao guardar o estado persistente: " + e.getMessage());
        }
    }

    // Requisito 03: Lida com reinícios carregando o histórico sem duplicar
    private void loadState() {
        File file = new File(storageFilePath);
        if (!file.exists()) return;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            history.clear();
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    history.add(Observation.deserialize(line.trim()));
                }
            }
        } catch (IOException e) {
            System.out.println("Erro ao carregar o estado persistente: " + e.getMessage());
        }
    }

    // Adiciona uma nova observação e verifica se a tendência ocorreu
    public boolean addObservation(double value) {
        Observation newObs = new Observation(LocalDateTime.now(), value);
        history.add(newObs);
        saveState(); // Persiste imediatamente

        // Requisito 02 & 05: Verifica tendência de queda em 3 observações consecutivas
        if (history.size() >= requiredConsecutiveObservations) {
            int size = history.size();
            double v1 = history.get(size - 3).value;
            double v2 = history.get(size - 2).value;
            double v3 = history.get(size - 1).value;

            // Condição de tendência: decrescente em 3 checagens consecutivas
            if (v1 > v2 && v2 > v3) {
                System.out.println("\n🚨 TENDÊNCIA CONFIRMADA NO EXATO MOMENTO:");
                System.out.println(" - " + history.get(size - 3));
                System.out.println(" - " + history.get(size - 2));
                System.out.println(" - " + history.get(size - 1));
                return true;
            }
        }
        return false;
    }

    // Requisito 04: Mostra o histórico armazenado
    public void printHistory() {
        System.out.println("\n--- Histórico Armazenado (Persistente) ---");
        for (Observation obs : history) {
            System.out.println(" " + obs);
        }
    }

    public static void main(String[] args) {
        // Limpar ficheiro anterior para demonstração limpa
        new File("trend_state.csv").delete();

        TrendWatcherAgent agent = new TrendWatcherAgent();

        System.out.println("=== TESTE 1: Pico isolado (Não deve disparar tendência) ===");
        agent.addObservation(100.0);
        agent.addObservation(90.0);
        agent.addObservation(115.0); // Pico isolado (subida)
        agent.addObservation(110.0);
        
        agent.printHistory();

        System.out.println("\n=== TESTE 2: Tendência confirmada em 3 observações consecutivas ===");
        agent.addObservation(105.0);
        agent.addObservation(100.0); // 1ª descida
        agent.addObservation(95.0);  // 2ª descida
        boolean triggered = agent.addObservation(90.0);  // 3ª descida -> Dispara tendência!

        System.out.println("Tendência disparada? " + (triggered ? "SIM ✅" : "NÃO ❌"));

        // Requisito 03: Simular reinício do agente e verificar persistência
        System.out.println("\n=== TESTE 3: Simulação de Reinício (A carregar do armazenamento persistente) ===");
        TrendWatcherAgent restartedAgent = new TrendWatcherAgent();
        restartedAgent.printHistory();
    }
}
