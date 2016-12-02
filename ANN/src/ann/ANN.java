package ann;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by nathael on 29/11/16.
 */
public class ANN implements Serializable {
    private final List<List<SettableNeuron>> inputNeurons = new ArrayList<>();
    private final Neuron lastNeuron;

    private ANN(List<String> save) {
        Map<String, Neuron> nids = new HashMap<>();

        // fixed neurons
        String line = save.get(1).substring(
                save.get(1).indexOf("\"fixed\":[")+"\"fixed\":[".length(),
                save.get(1).length()-"],".length());
        {
            String[] fixedNeurons = line.split(";");
            for(String s : fixedNeurons) {
                // s= "{\"id\":\"" + sn + "\",\"value\":" + sn.getOutput() + "}"
                SettableNeuron sn = new SettableNeuron();

                String id = s.substring(
                        "{\"id\":\"".length(),
                        s.lastIndexOf("\",\"value\""));

                double value = Double.valueOf(s.substring(
                        s.indexOf("\"value\":")+"\"value\":".length(),
                        s.lastIndexOf("}")));

                sn.setValue(value);

                nids.put(id, sn);
            }
        }

        // inputs neurons
        line = save.get(2).substring(
                save.get(2).indexOf("\"inputs\":[")+"\"inputs\":[".length(),
                save.get(2).length()-"],".length());
        {
            String[] neurons = line.split(";");
            for(String s : neurons) {
                // s= "[" + String.join("!",lineInput) + "]"

                String[] inputs = s.substring(
                        "[".length(),
                        s.length()-"]".length())
                        .split("!");

                List<SettableNeuron> list = new ArrayList<>();
                for(String input : inputs) {
                    // input= "{\"id\":\"" + anInputNeuron + "\"}"
                    SettableNeuron sn = new SettableNeuron();
                    String id = input.substring(
                            input.indexOf("\"id\":\"")+"\"id\":\"".length(),
                            input.lastIndexOf("\"}"));

                    nids.put(id, sn);
                    list.add(sn);
                }

                inputNeurons.add(list);
            }
        }

        // hidden layer neurons
        line = save.get(3).substring("\"neurons\":[".length(), save.get(3).length()-"]}".length());
        Map<Neuron, Map<String, Double>> links = new HashMap<>();
        {
            String[] inputLines = line.split(";");
            for(String s : inputLines) {
                // s= "{\"id\":\""+n+"\",\"parents\":[" + String.join("!",subRes) + "]}"

                String id = s.substring(
                        s.indexOf("{\"id\":\"")+"{\"id\":\"".length(),
                        s.indexOf("\",\"parents\""));

                Neuron n = new Neuron();
                nids.put(id, n);

                String[] parentsStr = s.substring(
                        s.indexOf("\"parents\":[")+"\"parents\":[".length(),
                        s.lastIndexOf("]}"))
                        .split("!");

                Map<String,Double> parentsMap = new HashMap<>();
                links.put(n, parentsMap);
                for(String parentStr : parentsStr) {
                    // "{\""+entry.getKey()+"\":"+entry.getValue()+"}"
                    String idParent = parentStr.substring(
                            parentStr.indexOf("{\"")+"{\"".length(),
                            parentStr.indexOf("\":")
                    );
                    double synapseValue = Double.valueOf(parentStr.substring(
                            parentStr.indexOf("\":")+"\":".length(),
                            parentStr.lastIndexOf("}")
                    ));
                    parentsMap.put(idParent, synapseValue);
                }
            }
        }

        // lastNeuron
        line = save.get(0).substring("{\"last\":[\"".length(), save.get(0).length()-"\"],".length());
        lastNeuron = nids.get(line);

        // Synapses
        // Map<Neuron, Map<String, Double>> links = new HashMap<>();
        for(Map.Entry<Neuron, Map<String, Double>> entry : links.entrySet()) {
            for(Map.Entry<String,Double> parentEntry : entry.getValue().entrySet()) {
                Neuron parent = nids.get(parentEntry.getKey());
                entry.getKey().addParent(parent, parentEntry.getValue());
            }
        }
    }
    public ANN(int dataNb) {
        lastNeuron = new Neuron();
        SettableNeuron oneNeuron = new SettableNeuron();
        oneNeuron.setValue(1);

        final Set<Neuron> set6 = new HashSet<>();
        for(int i=0; i<20; i++) {
            Neuron n6 = new Neuron();
            set6.add(n6);
            lastNeuron.addParent(n6);
            n6.addParent(oneNeuron);
        }

        final Set<Neuron> set5 = new HashSet<>();
        for(int i=0; i<20; i++) {
            Neuron n5 = new Neuron();
            set5.add(n5);
            for(Neuron n6 : set6) {
                n6.addParent(n5);
            }
            n5.addParent(oneNeuron);
        }

        final List<List<Neuron>> set3 = new ArrayList<>();
        for(int j=0; j<dataNb; j++) {
            final List<Neuron> subset3 = new ArrayList<>();
            set3.add(subset3);

            Neuron nA = new Neuron(); subset3.add(nA);
            Neuron nB = new Neuron(); subset3.add(nB);
            Neuron nC = new Neuron(); subset3.add(nC);
            Neuron nD = new Neuron(); subset3.add(nD);
            Neuron nE = new Neuron(); subset3.add(nE);
            Neuron nF = new Neuron(); subset3.add(nF);

            nF.addParent(nD);
            nF.addParent(nE);
            nA.addParent(oneNeuron);
            nB.addParent(oneNeuron);
            nC.addParent(oneNeuron);
            nD.addParent(oneNeuron);
            nE.addParent(oneNeuron);
            nF.addParent(oneNeuron);

            for (Neuron n5 : set5) {
                n5.addParent(nA);
                n5.addParent(nB);
                n5.addParent(nC);
                n5.addParent(nF);
            }
        }

        final List<Neuron> set2 = new ArrayList<>();
        for(int i=0; i<9; i++) {
            Neuron n2 = new Neuron();
            set2.add(n2);
            for (Neuron n5 : set5) {
                n5.addParent(n2);
            }
            n2.addParent(oneNeuron);
        }

        // inputNeurons
        for(int i=0; i<dataNb; i++) {
            List<SettableNeuron> nLine = new ArrayList<>();
            inputNeurons.add(nLine);

            for(int j=0; j<9; j++) {
                SettableNeuron nj = new SettableNeuron();
                nLine.add(nj);

                set2.get(j).addParent(nj);
            }

            // 0 < 04578
            // 1 < 13568
            // 2 < 23467
            // 3 < 345
            // 4 < 678
            set3.get(i).get(0).addParent(nLine.get(0), nLine.get(4), nLine.get(5), nLine.get(7), nLine.get(8));
            set3.get(i).get(1).addParent(nLine.get(1), nLine.get(3), nLine.get(5), nLine.get(6), nLine.get(8));
            set3.get(i).get(2).addParent(nLine.get(2), nLine.get(3), nLine.get(4), nLine.get(6), nLine.get(7));
            set3.get(i).get(3).addParent(nLine.get(3), nLine.get(4), nLine.get(5));
            set3.get(i).get(4).addParent(nLine.get(6), nLine.get(7), nLine.get(8));
        }
    }

    public double getOutputFor(float[][] dataTable) throws ExecutionException, InterruptedException, IOException {
        lastNeuron.prepareAll();
        setInputNeurons(dataTable);

        return lastNeuron.getOutput();
    }

    public double learn(float[][] dataTable, double neededValue) throws ExecutionException, InterruptedException, IOException {
        lastNeuron.prepareAll();
        setInputNeurons(dataTable);

        lastNeuron.learn(neededValue);

        // compute distance
        lastNeuron.prepareAll();
        double distance = (lastNeuron.getOutput() - neededValue); // between -1 & 1

        return distance*distance; // between 0 & 1
    }

    private void setInputNeurons(float[][] dataTable) throws IOException {
        for(int i=0; i<dataTable.length; i++) {
            float[] dataLine = dataTable[i];
            for(int j=0; j<dataLine.length; j++) {
                inputNeurons.get(i).get(j).setValue(dataLine[j]);
            }
        }
    }

    public List<String> save() {
        Map<Neuron, String> ids = new HashMap<>();

        Stack<Neuron> stack = new Stack<>();
        stack.push(lastNeuron);
        Set<SettableNeuron> settableNeurons = new HashSet<>();

        Set<String> neurons = new HashSet<>();
        while(!stack.isEmpty()) {
            Neuron n = stack.pop();
            if(n instanceof SettableNeuron) {
                settableNeurons.add((SettableNeuron) n);
                continue;
            }

            ids.put(n, n.toString());

            Set<String> subRes = new HashSet<>();
            for(Map.Entry<Neuron, Double> entry : n.getParents().entrySet()) {
                subRes.add("{\""+entry.getKey()+"\":"+entry.getValue()+"}");
                if(!(stack.contains(entry.getKey()) || ids.containsKey(entry.getKey())))
                    stack.push(entry.getKey());
            }
            neurons.add("{\"id\":\""+n+"\",\"parents\":[" + String.join("!",subRes) + "]}");
        }

        Set<String> inputs = new HashSet<>();
        for (List<SettableNeuron> inputNeuron : inputNeurons) {
            Set<String> lineInput = new HashSet<>();
            for (SettableNeuron anInputNeuron : inputNeuron) {
                settableNeurons.remove(anInputNeuron);
                lineInput.add("{\"id\":\"" + anInputNeuron + "\"}");
            }
            inputs.add("[" + String.join("!",lineInput) + "]");
        }

        Set<String> fixedNeurons = new HashSet<>();
        try {
            for (SettableNeuron sn : settableNeurons) {
                fixedNeurons.add("{\"id\":\"" + sn + "\",\"value\":" + sn.getOutput() + "}");
            }
        }catch (InterruptedException | ExecutionException ignored) {}

        List<String> res = new ArrayList<>();
        res.add("{\"last\":[\""+lastNeuron+"\"],");
        res.add("\"fixed\":[" + String.join(";", fixedNeurons) + "],");
        res.add("\"inputs\":["+String.join(";",inputs)+"],");
        res.add("\"neurons\":[" + String.join(";",neurons) + "]}");
        return res;
    }

    public static ANN load(List<String> save) {
       return new ANN(save);
    }
}
