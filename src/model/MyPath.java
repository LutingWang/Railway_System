package model;

import com.oocourse.specs1.models.Path;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.min;

public class MyPath implements Path {
    private int size;
    private Integer[] nodes;
    private List<Integer> nodeList;
    
    private Integer distinctNodeCount = null;
    
    public MyPath(int... nodeList) {
        size = nodeList.length;
        nodes = new Integer[size];
        for (int i = nodeList.length - 1; i >= 0; i--) {
            nodes[i] = nodeList[i];
        }
        this.nodeList = Arrays.asList(nodes);
    }
    
    public List<Integer> getNodeList() {
        return nodeList;
    }
    
    @Override
    public int size() {
        return size;
    }
    
    @Override
    public int getNode(int index) {
        return nodes[index];
    }
    
    @Override
    public boolean containsNode(int node) {
        return nodeList.contains(node);
    }
    
    @Override
    public int getDistinctNodeCount() {
        if (distinctNodeCount == null) {
            distinctNodeCount = (int) nodeList.stream().distinct().count();
        }
        return distinctNodeCount;
    }
    
    @Override
    public boolean isValid() {
        return size() >= 2;
    }
    
    @Override
    public Iterator<Integer> iterator() {
        return nodeList.iterator();
    }
    
    @Override
    public int compareTo(Path path) {
        int size = min(this.size(), path.size());
        int result;
        for (int i = 0; i < size; i++) {
            result = Integer.compare(nodes[i], path.getNode(i));
            if (result != 0) {
                return result;
            }
        }
        return this.size() - path.size();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Path) {
            Path other = (Path) obj;
            if (this.size() != other.size()) {
                return false;
            }
            for (int i = size() - 1; i >= 0; i--) {
                if (!nodes[i].equals(other.getNode(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return nodes[0] + nodes[1] + size;
    }
    
    @Override
    public String toString() {
        return nodeList.toString();
    }
}
