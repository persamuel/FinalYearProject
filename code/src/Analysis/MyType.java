package Analysis;

public abstract class MyType {
    public static class Char_T extends MyType {

    }

    public static class Int_T extends MyType {

    }

    public static class Bool_T extends MyType {

    }

    public static class Unit_T extends MyType {

    }

    public static class IntStackArray_T extends MyType {
        private final int size;

        public IntStackArray_T(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public static class CharStackArray_T extends MyType {
        private final int size;

        public CharStackArray_T(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }
    }

    public static class IntHeapArray_T extends MyType {

    }

    public static class CharHeapArray_T extends MyType {

    }

    public static class StringArray_T extends MyType {

    }
}
