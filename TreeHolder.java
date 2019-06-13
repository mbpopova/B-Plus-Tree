import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class TreeHolder {
	
	private Node top;
	
	public void buildTree() {
		Map<Integer, Object> records = new HashMap<Integer, Object>();
		
		records.put(5, 25);
		
		Iterator<Map.Entry<Integer, Object>> iter = records.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry<Integer, Object> me = (Map.Entry<Integer, Object>)iter.next();
			
			top.insert(me.getKey(), me.getValue());
		}
	}
	
	public void printTree() {
		top.print();
	}
	
	private void _buildTree() {
		top = new Node();
		for (int i = 3 ; i < 15; i++) {
			top = top.insert(i, i * 10);
		}
	}
	public static void main (String args[]) {
		TreeHolder th = new TreeHolder();
		th._buildTree();
		th.printTree();
	/*
		th.top = th.top.insert(5, 25);
		th.top = th.top.insert(6, 36);
		th.top = th.top.insert(7, 49);
		th.top = th.top.insert(8, 64);
		th.top = th.top.insert(9, 81);
		th.top = th.top.insert(10, 100);
		th.top = th.top.insert(11, 121);
		th.top = th.top.insert(12, 144);
		th.top = th.top.insert(13, 169);
		th.top = th.top.insert(14, 196);
		th.printTree();
		
		*/
	}

}

class Node {
	private static int CAPACITY = 3;
	
	private boolean isLeaf = true;
	private Node parent;
	
	private int numKeys;
	
	private List<Integer> keys = new ArrayList<Integer>(CAPACITY + 1);
	private List<Object> refs = new ArrayList<Object> (CAPACITY + 2);  // just optimization since lists can grow
	
	public Node() {
		//keys = new int[CAPACITY + 1]; // to accommodate extra key before split
		//refs = new Object[keys.length  + 1]; 
	}
	
	public void print() {
		print(0);
	}

	private void print(int level) {

		for (int i = 0; i < level * 5; i++) {
			System.out.print('-');
		}

		// Print this Node's keys:
		for (int i = 0; i < this.numKeys; i++) {
			System.out.print(this.keys.get(i));
			System.out.print(' ');
		}
		System.out.println(); // separate Node
		level = level + 1;
		for (int j = 0; j < refs.size(); j++) {

			if (refs.get(j) instanceof Node) {
				Node child = (Node) refs.get(j);
				child.print(level);
			} else {
				// Print values:
				// System.out.print(refs.get(j));
				// System.out.print(' ');
			}

		}
	}

	private void makeRoot(Node rt) {
		this.parent = new Node();
		rt.parent = this.parent;
		this.parent.refs.add(this);
		
		parent.isLeaf = false;
	}
	
	private void wedge(int key, Object value) {
		int keyLoc = find(key);
		int refLoc = isLeaf ? keyLoc : keyLoc + 1;  // because rt will be inserted for a key

		if (keyLoc > keys.size() - 1) {
			keys.add(key);
		} else {
			keys.add(keyLoc, key);
		}
		
		if (refLoc > refs.size() - 1) {
			refs.add(value);
		} else {
			refs.add(refLoc, value);
		}
		
		numKeys = keys.size();
	}
	
	private Node split() {
		// set numKeys for left sibling
		this.numKeys = (int) CAPACITY / 2 + 1;

		
		Node right = new Node();
		right.isLeaf = this.isLeaf; // should have the same flag as left node
		right.keys.addAll(this.keys.subList(numKeys, this.keys.size())); // copy from this node's key starting at numKeys and to the last key
		right.refs.addAll(this.refs.subList(numKeys, this.refs.size()));
		right.numKeys = right.keys.size(); // initial numKeys
		right.parent = this.parent;
		
		for (int i = 0 ; i < refs.size(); i++) {
			if (refs.get(i) instanceof Node) {
				((Node)refs.get(i)).parent = right;
			}
		}
	
	    this.refs = refs.subList(0, numKeys);
	    this.keys = keys.subList(0, numKeys);
	    
		//return right sibling
		return right;
	}
	
	

	private Node getRoot() {
		if (parent == null) {
			return this;
		}
		
		return parent.getRoot();
	}
	private Node doInsert(int key, Object value) {

	
		this.wedge(key, value);
		if (keys.size() > CAPACITY) {
			Node rt = this.split();
			
			if (this.parent == null) {
				makeRoot(rt);
			}
			int keyGoesUp = this.keys.get(this.numKeys - 1);
			parent.doInsert(keyGoesUp, rt);
		}
		
		return getRoot();
				//parent != null ? parent : this;
	}
	
	public Node insert(Integer key, Object value) {
			Node leaf = getLeaf(key);
			return leaf.doInsert(key, value);	// highest node
	}
	
	private int find(int key) {
		for (int i = 0 ; i < numKeys; i++) {
			if (key < keys.get(i)) return i;
		}
		return numKeys;
	}
	
	private Node getLeaf(int key) {
		if (this.isLeaf) return this;
		
		Node child = (Node) refs.get(find(key));
		
		return child.getLeaf(key);
	}
	
	public String toString() {
		String str = "[";
		for (int i = 0; i < this.numKeys; i++) {
			str = str + keys.get(i) +  ' ';
		}
		return str + "]";
	}
}