package Word2Vec.util;

import java.util.Collection;
import java.util.TreeSet;

import Word2Vec.domain.HiddenNeuron;
import Word2Vec.domain.Neuron;

/**
 * 构建Haffman编码树
 * @author ansj
 *
 */
public class Haffman {
    private int layerSize;

    public Haffman(int layerSize) {
        this.layerSize = layerSize;
    }

    private TreeSet<Neuron> set = new TreeSet<>();

    public void make(Collection<Neuron> neurons) {
        set.addAll(neurons);
        while (set.size() > 1) {
            merger();
        }
    }


    private void merger() {
        // TODO Auto-generated method stub
        HiddenNeuron hn = new HiddenNeuron(layerSize);
        Neuron min1 = set.pollFirst();
        Neuron min2 = set.pollFirst();
        hn.freq = min1.freq + min2.freq;
        min1.parent = hn;
        min2.parent = hn;
        min1.code = 0;
        min2.code = 1;
        set.add(hn);
    }
    
}
