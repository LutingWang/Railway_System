package model;

import com.oocourse.specs1.models.Path;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.min;

public class MyPath implements Path {
    private List<Integer> nList;
    
    public MyPath(int... nodeList) {
        Integer[] temp = new Integer[nodeList.length];
        for (int i = nodeList.length - 1; i >= 0; i--) {
            temp[i] = nodeList[i];
        }
        nList = Arrays.asList(temp);
    }
    
    @Override
    public int size() {
        return nList.size();
    }
    
    @Override
    public int getNode(int index) {
        return nList.get(index);
    }
    
    @Override
    public boolean containsNode(int node) {
        return nList.contains(node);
    }
    
    @Override
    public int getDistinctNodeCount() {
        return (int) nList.stream().distinct().count();
    }
    
    @Override
    public boolean isValid() {
        return size() >= 2;
    }
    
    @Override
    public Iterator<Integer> iterator() {
        return nList.iterator();
    }
    
    @Override
    public int compareTo(Path path) {
        int size = min(this.size(), path.size());
        int result;
        for (int i = 0; i < size; i++) {
            result = Integer.compare(nList.get(i), path.getNode(i));
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
                if (!nList.get(i).equals(other.getNode(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }
    
    @Override
    public String toString() {
        return nList.toString();
    }
}
