
import java.util.ArrayList;
import java.util.List;
import java.util.Comparator;

public class BPlusTree {

	public Block root;
	public NodeComparator cmp = new NodeComparator();
	public boolean isEmpty = true;
	public int numlevels;

	public static final int order = 4;
	// order = no. of pointers

	public BPlusTree(Block root) {
		this.root = root;
		root.setParent(null);
	}

	public Block search( Datanode n ) {
		//First, the current block is examined, looking for the smallest i
		//such that search-key value K i is greater than or equal to V, the search-key value
		Block temp = root;
		int i;
		while(!(temp instanceof LeafBlock)){
			for( i = 0; i < temp.getNodes().size(); i++ ){
				if( cmp.compare( n, temp.getNodes().get(i) ) < 0 ){
					//n is smaller, hence go into left pointer
					temp = temp.getChildren().get(i);
					break;
				}
				if( cmp.compare( n, temp.getNodes().get(i) ) == 0 ){
					//n is equal to the search key, go into right pointer
					temp = temp.getChildren().get(i+1);
					break;
				}
				//else go right
				//else if there is no remaining node to evaluate, set temp = last non-null pointer
				if( i == temp.getNodes().size()-1 ){
					temp = temp.getChildren().get(i+1);
					break;
				}
			}
		}
//		for( i = 0; i < temp.getNodes().size(); i++ ){
//			if( temp.getNodes().get(i).equals(n)){
//				return temp;
//			}
//		}
		return temp; //the block where the node n should be
	}

	public void insert( Datanode n ) {
		if(isEmpty ){
			isEmpty = false;
			Block root = new LeafBlock(null, null); //create an empty leaf 
			this.root = root;
		}
		else{
			Block searched = search( n );
			if( searched.getNodes().size() < BPlusTree.order-1 ){
				//the block where it should be has < order-1 nodes
				searched.insertNode(n);
			}
			else{
				//the block where it should be has order-1 nodes already, so we have to split
			}
		}
	}

	public void printTree() {
		int i, j;
		for( i = 0; i < numlevels; i++ ){
			
		}
	}

	public void TopDown() {

	}

	public void BottomUp() {

	}

	public static void main(String args[]) {

	}

}

class Datanode {
	// single datanode
	String data;

	public Datanode(String data) {
		this.data = data;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
}

class NodeComparator implements Comparator{

	@Override
	public int compare(Object o1, Object o2) {
		// TODO Auto-generated method stub
		Datanode d1 = (Datanode)o1;
		Datanode d2 = (Datanode)o2;
		return( d1.data.compareTo(d2.data) );
		// a negative number is returned if d1 < d2
	}
	
}

class Block {

	// a block consisting of (order-1) datanodes
	List<Datanode> nodes;
	List<Block> children;
	Block parent;

	public List<Datanode> getNodes() {
		return nodes;
	}

	public void setNodes(List<Datanode> nodes) {
		this.nodes = nodes;
	}

	public List<Block> getChildren() {
		return children;
	}

	public void setChildren(List<Block> children) {
		this.children = children;
	}

	public Block getParent() {
		return parent;
	}

	public void setParent(Block parent) {
		this.parent = parent;
	}

	public Block(Block parent) {
		nodes = new ArrayList<Datanode>(BPlusTree.order - 1);
		children = new ArrayList<Block>(BPlusTree.order);
		this.parent = parent;
	}

	public void insertNode(Datanode d) {
		// handles the block structure
		//need to write this piece of code
		nodes.add(d);
	}
	
	public void insertInParent(){
		//when 
	}

	public void deleteNode(int index) {
		// simply removes the node. This does not do anything to the tree
		// structure
		nodes.remove(index);
	}
}

class LeafBlock extends Block {
	// block consisting of the actual data
	Block nextBlock;

	public LeafBlock(Block parent, Block nextBlock) {
		super(parent);
		this.nextBlock = nextBlock;
	}
	
	public void insertInLeaf( Datanode n ){
		
	}
}
