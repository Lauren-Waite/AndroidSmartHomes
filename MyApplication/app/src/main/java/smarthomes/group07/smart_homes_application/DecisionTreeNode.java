package smarthomes.group07.smart_homes_application;

/**
 * Created by laurenwaite on 4/22/16.
 */
public abstract class DecisionTreeNode {
    protected DecisionTreeNode left;
    protected DecisionTreeNode right;

    public DecisionTreeNode() {

    }

    public abstract String getNodeName();

    public void setLeft(DecisionTreeNode dt) {
        left = dt;
    }


    public void setRight(DecisionTreeNode dt) {
        right = dt;
    }

    public DecisionTreeNode getLeft() {
        return left;
    }

    public DecisionTreeNode getRight() {
        return right;
    }

    public boolean hasLeft() {
        if(left == null) {
            return false;
        }
        return true;
    }

    public boolean hasRight() {
        if(right == null) {
            return false;
        }
        return true;
    }
    public boolean hasTwoChildren()  {
        return (right != null && left != null);
    }

    public abstract DecisionTreeNode analyzeNode(float v);

}

