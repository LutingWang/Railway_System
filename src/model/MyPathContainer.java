package model;

import com.oocourse.specs1.models.Path;
import com.oocourse.specs1.models.PathContainer;
import com.oocourse.specs1.models.PathIdNotFoundException;
import com.oocourse.specs1.models.PathNotFoundException;

import java.util.HashMap;
import java.util.function.Function;
import java.util.stream.Stream;

public class MyPathContainer implements PathContainer {
    private HashMap<Path, Integer> p2id = new HashMap<>();
    private HashMap<Integer, Path> id2p = new HashMap<>();
    
    private int pid = 1;
    private Integer distNodeCountCache = null;
    
    public MyPathContainer() {}
    
    @Override
    public int size() {
        return p2id.size();
    }
    
    @Override
    public boolean containsPath(Path path) {
        return p2id.containsKey(path);
    }
    
    @Override
    public boolean containsPathId(int pathId) {
        return id2p.containsKey(pathId);
    }
    
    @Override
    public Path getPathById(int pathId) throws PathIdNotFoundException {
        Path p = id2p.get(pathId);
        if (p == null) {
            throw new PathIdNotFoundException(pathId);
        } else {
            return p;
        }
    }
    
    @Override
    public int getPathId(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid()) {
            throw new PathNotFoundException(path);
        }
        Integer id = p2id.get(path);
        if (id == null) {
            throw new PathNotFoundException(path);
        } else {
            return id;
        }
    }
    
    @Override
    public int addPath(Path path) {
        if (path == null || !path.isValid()) {
            return 0;
        }
        Integer id = p2id.get(path);
        if (id == null) {
            distNodeCountCache = null;
            p2id.put(path, pid);
            id2p.put(pid, path);
            return pid++;
        } else {
            return id;
        }
    }
    
    @Override
    public int removePath(Path path) throws PathNotFoundException {
        if (path == null || !path.isValid()) {
            throw new PathNotFoundException(path);
        }
        Integer id = p2id.get(path);
        if (id == null) {
            throw new PathNotFoundException(path);
        } else {
            distNodeCountCache = null;
            p2id.remove(path);
            id2p.remove(id);
            return id;
        }
    }
    
    @Override
    public void removePathById(int pathId) throws PathIdNotFoundException {
        Path p = id2p.get(pathId);
        if (p == null) {
            throw new PathIdNotFoundException(pathId);
        } else {
            distNodeCountCache = null;
            p2id.remove(p);
            id2p.remove(pathId);
        }
    }
    
    @Override
    public int getDistinctNodeCount() {
        if (distNodeCountCache == null) {
            Function<Path, Stream<Integer>> mapper = path -> {
                if (path instanceof MyPath) {
                    return ((MyPath) path).getNodeList().stream();
                } else {
                    Integer[] temp = new Integer[path.size()];
                    for (int i = path.size() - 1; i >= 0; i--) {
                        temp[i] = path.getNode(i);
                    }
                    return Stream.of(temp);
                }
            };
            distNodeCountCache = (int) p2id.keySet()
                    .stream().flatMap(mapper).distinct().count();
        }
        return distNodeCountCache;
    }
    
    @Override
    public String toString() {
        return id2p.toString();
    }
}
