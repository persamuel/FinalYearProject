package Analysis;

public abstract class Type {
    public static class Char_T extends Type {

    }

    public static class Int_T extends Type {

    }

    public static class Bool_T extends Type {

    }

    public static class Unit_T extends Type {

    }

    public static class IntStackArray_T extends Type {
        private final int size;

        public IntStackArray_T(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public static class CharStackArray_T extends Type {
        private final int size;

        public CharStackArray_T(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public static class IntHeapArray_T extends Type {

    }

    public static class CharHeapArray_T extends Type {

    }
}
