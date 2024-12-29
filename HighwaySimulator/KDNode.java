import HaltingPointDataParser.HaltingPointInstance;

import java.util.List;
import java.util.ArrayList;
import java.util.PriorityQueue;

// KD-Tree Node
class KDNode {
    HaltingPointInstance point;
    KDNode left, right;
    boolean isVerticalSplit;

    KDNode(HaltingPointInstance point, boolean isVerticalSplit) {
        this.point = point;
        this.isVerticalSplit = isVerticalSplit;
    }
}

// KD-Tree
class KDTree {
    private KDNode root;

    public KDTree(List<HaltingPointInstance> points) {
        root = buildTree(points, 0);
    }

    // Recursive KD-Tree construction
    private KDNode buildTree(List<HaltingPointInstance> points, int depth) {
        if (points.isEmpty()) return null;

        int axis = depth % 2; // Alternate between X and Y axes
        List<HaltingPointInstance> mutablePoints = new ArrayList<>(points);
        mutablePoints.sort((p1, p2) -> axis == 0
                ? Double.compare(p1.getHaltingPointLatitude(), p2.getHaltingPointLatitude())
                : Double.compare(p1.getHaltingPointLongitude(), p2.getHaltingPointLongitude()));

        int median = points.size() / 2;
        HaltingPointInstance medianPoint = points.get(median);

        KDNode node = new KDNode(medianPoint, axis == 0);
        node.left = buildTree(points.subList(0, median), depth + 1);
        node.right = buildTree(points.subList(median + 1, points.size()), depth + 1);

        return node;
    }

    // Nearest neighbor search
    public HaltingPointInstance findNearest(HaltingPointInstance targetPoint) {
        return findNearest(root, targetPoint, null, Double.MAX_VALUE);
    }

    private HaltingPointInstance findNearest(KDNode node, HaltingPointInstance target, HaltingPointInstance best, double bestDist) {
        if (node == null) return best;

        double dist = squaredDistance(node.point, target);
        if (dist < bestDist) {
            best = node.point;
            bestDist = dist;
        }

        double splitDist = node.isVerticalSplit
                ? target.getHaltingPointLatitude() - node.point.getHaltingPointLatitude()
                : target.getHaltingPointLongitude() - node.point.getHaltingPointLongitude();

        KDNode first = splitDist < 0 ? node.left : node.right;
        KDNode second = splitDist < 0 ? node.right : node.left;

        best = findNearest(first, target, best, bestDist);

        if (splitDist * splitDist < bestDist) {
            best = findNearest(second, target, best, bestDist);
        }

        return best;
    }

    private double squaredDistance(HaltingPointInstance p1, HaltingPointInstance p2) {
        double dx = p1.getHaltingPointLatitude() - p2.getHaltingPointLatitude();
        double dy = p1.getHaltingPointLongitude() - p2.getHaltingPointLongitude();
        return dx * dx + dy * dy;
    }
}

