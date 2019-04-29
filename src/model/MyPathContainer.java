package model;

import com.oocourse.specs1.models.Path;
import com.oocourse.specs1.models.PathContainer;
import com.oocourse.specs1.models.PathIdNotFoundException;
import com.oocourse.specs1.models.PathNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class MyPathContainer implements PathContainer {
    private ArrayList<Path> pList = new ArrayList<>();
    private ArrayList<Integer> pidList = new ArrayList<>();
    private int pid = 1; // TODO: check algorithm for pid generating
    
    public MyPathContainer() {}
    
    @Override
    public int size() {
        return pList.size();
    }
    
    @Override
    public boolean containsPath(Path path) {
        return pList.contains(path);
    }
    
    @Override
    public boolean containsPathId(int pathId) {
        return pidList.contains(pathId);
    }
    
    @Override
    public Path getPathById(int pathId) throws PathIdNotFoundException {
        int ind = pidList.indexOf(pathId);
        if (ind == -1) {
            throw new PathIdNotFoundException(pathId);
        } else {
            return pList.get(ind);
        }
    }
    
    @Override
    public int getPathId(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid()) {
            throw new PathNotFoundException(path);
        }
        int ind = pList.indexOf(path);
        if (ind == -1) {
            throw new PathNotFoundException(path);
        } else {
            return pidList.get(ind);
        }
    }
    
    @Override
    public int addPath(Path path) {
        if (path == null || !path.isValid()) {
            return 0;
        }
        int ind = pList.indexOf(path);
        if (ind == -1) {
            pList.add(path);
            pidList.add(pid);
            return pid++;
        } else {
            return pidList.get(ind);
        }
    }
    
    @Override
    public int removePath(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid()) {
            throw new PathNotFoundException(path);
        }
        int ind = pList.indexOf(path);
        if (ind == -1) {
            throw new PathNotFoundException(path);
        } else {
            pList.remove(ind);
            return pidList.remove(ind);
        }
    }
    
    @Override
    public void removePathById(int pathId) throws PathIdNotFoundException {
        int ind = pidList.indexOf(pathId);
        if (ind == -1) {
            throw new PathIdNotFoundException(pathId);
        } else {
            pList.remove(ind);
            pidList.remove(ind);
        }
    }
    
    @Override
    public int getDistinctNodeCount() {
        Function<Path, Stream<Integer>> mapper = path -> {
            Integer[] temp = new Integer[path.size()];
            for (int i = path.size() - 1; i >= 0; i--) {
                temp[i] = path.getNode(i);
            }
            return Stream.of(temp);
        };
        return (int) pList.stream().flatMap(mapper).distinct().count();
    }
    
    @Override
    public String toString() {
        HashMap<Integer, Path> temp = new HashMap<>();
        for (int i = 0; i < pList.size(); i++) {
            temp.put(pidList.get(i), pList.get(i));
        }
        return temp.toString();
    }
}
