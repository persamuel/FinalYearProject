package Analysis;

public abstract class Types {
    public static class Char_T extends Types {

    }

    public static class Int_T extends Types {

    }

    public static class Bool_T extends Types {

    }

    public static class Unit_T extends Types {

    }

    public static class IntStackArray_T extends Types {
        private final int size;

        public IntStackArray_T(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public static class CharStackArray_T extends Types {
        private final int size;

        public CharStackArray_T(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public static class IntHeapArray_T extends Types {

    }

    public static class CharHeapArray_T extends Types {

    }
}
