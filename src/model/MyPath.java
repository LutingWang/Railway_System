package model;

import com.oocourse.specs3.models.Path;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import static java.lang.Math.min;

public class MyPath implements Path {
    private List<Integer> nodeList;
    private HashSet<Integer> distinctNodes = null;
    
    public MyPath(int... nodeList) {
        Integer[] nodes = new Integer[nodeList.length];
        for (int i = nodeList.length - 1; i >= 0; i--) {
            nodes[i] = nodeList[i];
        }
        this.nodeList = Arrays.asList(nodes);
    }
    
    List<Integer> getNodeList() {
        return nodeList;
    }
    
    Set<Integer> getDistinctNodes() {
        return distinctNodes;
    }
    
    @Override
    public int size() {
        return nodeList.size();
    }
    
    @Override
    public int getNode(int index) {
        return nodeList.get(index);
    }
    
    @Override
    public boolean containsNode(int node) {
        if (distinctNodes == null) {
            return nodeList.contains(node);
        } else {
            return distinctNodes.contains(node);
        }
    }
    
    @Override
    public int getDistinctNodeCount() {
        if (distinctNodes == null) {
            distinctNodes = new HashSet<>(nodeList);
        }
        return distinctNodes.size();
    }
    
    @Override
    public boolean isValid() {
        return size() >= 2;
    }
    
    @Override
    public int getUnpleasantValue(int nodeId) {
        return 0;
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
            result = Integer.compare(nodeList.get(i), path.getNode(i));
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
                if (!nodeList.get(i).equals(other.getNode(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override // TODO: check if there is other class extends path
    public int hashCode() {
        return nodeList.get(0) + nodeList.get(0) + size();
    }
    
    @Override
    public String toString() {
        return nodeList.toString();
    }
}
