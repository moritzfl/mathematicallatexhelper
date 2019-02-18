package mathpix.api.response;

import java.util.ArrayList;

/**
 * Created by admin on 3/19/17.
 *
 *
 {
     "detection_list": [],
     "detection_map": {
         "contains_chart": 0,
         "contains_diagram": 0,
         "contains_geometry": 0,
         "contains_graph": 0,
         "contains_table": 0,
         "is_inverted": 0,
         "is_not_math": 0,
         "is_printed": 0
     },
     "error": "",
     "latex": "\\lim _ { x \\rightarrow 3} ( \\frac { x ^ { 2} + 9} { x - 3} )",
     "latex_confidence": 0.86757309488734,
     "position": {
         "height": 273,
         "top_left_x": 57,
         "top_left_y": 14,
         "width": 605
     }
 }
 */

public class DetectionResult {
    public DetectionMap detection_map;
    public String error;
    public String latex;
    public ArrayList<String> latex_list;
    public double latex_confidence;
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

    public DetectionMap getDetection_map() {
        return detection_map;
    }

    public String getError() {
        return error;
    }

    public String getLatex() {
        return latex;
    }

    public ArrayList<String> getLatex_list() {
        return latex_list;
    }

    public double getLatex_confidence() {
        return latex_confidence;
    }

    public Position getPosition() {
        return position;
    }

    public static class DetectionMap {
        public double contains_chat;
        public double contains_diagram;
        public double contains_geometry;
        public double contains_graph;
        public double contains_table;

        public double is_inverted;
        public double is_not_math;
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

        public double getContains_chat() {
            return contains_chat;
        }

        public double getContains_diagram() {
            return contains_diagram;
        }

        public double getContains_geometry() {
            return contains_geometry;
        }

        public double getContains_graph() {
            return contains_graph;
        }

        public double getContains_table() {
            return contains_table;
        }

        public double getIs_inverted() {
            return is_inverted;
        }

        public double getIs_not_math() {
            return is_not_math;
        }

        public double getIs_printed() {
            return is_printed;
        }
    }

    public static class Position {
        public double width;
        public double height;
        public double top_left_x;
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

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public double getTop_left_x() {
            return top_left_x;
        }

        public double getTop_left_y() {
            return top_left_y;
        }
    }
}
