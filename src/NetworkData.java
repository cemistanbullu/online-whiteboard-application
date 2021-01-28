import java.awt.Shape;
import java.io.Serializable;
import java.util.ArrayList;

public class NetworkData implements Serializable{

	String msg;
	Shape shape;
	boolean riseHand;
	Selection selection;
	

	
	public NetworkData() {
		this.msg = "";
		this.shape = null;
	}
	
	public NetworkData(String msg) {
		this.shape = null;
		this.msg = msg;
		this.selection = Selection.MESSAGE;
	}
	
	public NetworkData(Shape shape) {
		this.msg = "";
		this.shape = shape; 
		this.selection = Selection.SHAPE;
	}
	
	
	public NetworkData(boolean riseHand) {
		this.selection = Selection.HAND;
		this.riseHand = riseHand;
		this.msg = "";
		this.shape = null;
	}
	
	
	
	
}
