package smarthomes.group07.smart_homes_application;

/**
 * Created by laurenwaite on 5/1/16.
 */
public class ClassificationNode extends DecisionTreeNode {
    private String classification;

    public ClassificationNode(String c) {
        classification = c;
    }

    public String getNodeName() {
        return classification;
    }
    public DecisionTreeNode analyzeNode(float v) {
        return this;
    }
}