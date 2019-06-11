package mathpix.api.response;

import java.util.ArrayList;


/**
 * The type Detection result. This is a pure dataclass that can be mapped to the MathPix server response.
 *
 *
 *
 * @author Moritz Floeter
 */
public class DetectionResult {
    /**
     * The Detection map.
     */
    public DetectionMap detection_map;
    /**
     * The error message. This is empty, if no error occurred.
     */
    public String error;
    /**
     * The Latex expression.
     */
    public String latex;
    /**
     * The Latex list.
     */
    public ArrayList<String> latex_list;
    /**
     * The confidence in the result as value between 0 and 1 where 0 is the lowest and
     * 1 is the highest confidence.
     */
    public double latex_confidence;
    /**
     * The position of the latex expression on the photo.
     */
    public Position position;

    @Override
    public String toString() {
        return "DetectionResult{" +
                "detection_map=" + detection_map +
                ", error='" + error + '\'' +
                ", latex='" + latex + '\'' +
                ", latex_list=" + latex_list +
                ", latex_confidence=" + latex_confidence +
                ", position=" + position +
                '}';
    }

    /**
     * Gets detection map.
     *
     * @return the detection map
     */
    public DetectionMap getDetection_map() {
        return detection_map;
    }

    /**
     * Gets error.
     *
     * @return the error
     */
    public String getError() {
        return error;
    }

    /**
     * Gets latex.
     *
     * @return the latex
     */
    public String getLatex() {
        return latex;
    }

    /**
     * Gets latex list.
     *
     * @return the latex list
     */
    public ArrayList<String> getLatex_list() {
        return latex_list;
    }

    /**
     * Gets latex confidence.
     *
     * @return the latex confidence
     */
    public double getLatex_confidence() {
        return latex_confidence;
    }

    /**
     * Gets position.
     *
     * @return the position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Class to store information about the content that was detected.
     */
    public static class DetectionMap {
        /**
         * Estimated probability that the analyzed image contains a chat.
         */
        public double contains_chat;
        /**
         * Estimated probability that the analyzed image contains a diagram.
         */
        public double contains_diagram;
        /**
         * Estimated probability that the analyzed image contains a geometry elements.
         */
        public double contains_geometry;
        /**
         * Estimated probability that the analyzed image contains a graph.
         */
        public double contains_graph;
        /**
         * Estimated probability that the analyzed image contains a table.
         */
        public double contains_table;

        /**
         * Estimated probability that the analyzed image is inverted.
         */
        public double is_inverted;
        /**
         * Estimated probability that the analyzed image is not a mathematical expression.
         */
        public double is_not_math;
        /**
         * Estimated probability that the analyzed image is printed (in contrast to being handwritten).
         */
        public double is_printed;

        @Override
        public String toString() {
            return "DetectionMap{" +
                    "contains_chat=" + contains_chat +
                    ", contains_diagram=" + contains_diagram +
                    ", contains_geometry=" + contains_geometry +
                    ", contains_graph=" + contains_graph +
                    ", contains_table=" + contains_table +
                    ", is_inverted=" + is_inverted +
                    ", is_not_math=" + is_not_math +
                    ", is_printed=" + is_printed +
                    '}';
        }

        /**
         * Gets the estimated probability that the analyzed image contains a chat.
         *
         * @return the contains chat
         */
        public double getContains_chat() {
            return contains_chat;
        }

        /**
         * Gets the estimated probability that the analyzed image contains a diagram.
         *
         * @return the contains diagram
         */
        public double getContains_diagram() {
            return contains_diagram;
        }

        /**
         * Gets the estimated probability that the analyzed image contains a geometric element.
         *
         * @return the contains geometry
         */
        public double getContains_geometry() {
            return contains_geometry;
        }

        /**
         * Gets the estimated probability that the analyzed image contains a graph.
         *
         * @return the contains graph
         */
        public double getContains_graph() {
            return contains_graph;
        }

        /**
         * Gets the estimated probability that the analyzed image contains a table.
         *
         * @return the contains table
         */
        public double getContains_table() {
            return contains_table;
        }

        /**
         * Gets the estimated probability that the analyzed image is inverted.
         *
         * @return the is inverted
         */
        public double getIs_inverted() {
            return is_inverted;
        }

        /**
         * Gets the estimated probability that the analyzed image is not a mathematical expression.
         *
         * @return the is not math
         */
        public double getIs_not_math() {
            return is_not_math;
        }

        /**
         * Gets the estimated probability that the analyzed image is printed (in contrast to being handwritten).
         *
         * @return the is printed
         */
        public double getIs_printed() {
            return is_printed;
        }
    }

    /**
     * Position within the image.
     */
    public static class Position {
        /**
         * The Width.
         */
        public double width;
        /**
         * The Height.
         */
        public double height;
        /**
         * The Top left x.
         */
        public double top_left_x;
        /**
         * The Top left y.
         */
        public double top_left_y;

        @Override
        public String toString() {
            return "Position{" +
                    "width=" + width +
                    ", height=" + height +
                    ", top_left_x=" + top_left_x +
                    ", top_left_y=" + top_left_y +
                    '}';
        }

        /**
         * Gets width.
         *
         * @return the width
         */
        public double getWidth() {
            return width;
        }

        /**
         * Gets height.
         *
         * @return the height
         */
        public double getHeight() {
            return height;
        }

        /**
         * Gets top left x.
         *
         * @return the top left x
         */
        public double getTop_left_x() {
            return top_left_x;
        }

        /**
         * Gets top left y.
         *
         * @return the top left y
         */
        public double getTop_left_y() {
            return top_left_y;
        }
    }
}
