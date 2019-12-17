package Analysis;

public abstract class MyType {
    public static class Char_T extends MyType {
        @Override
        public String toString() {
            return "Char_T";
        }
    }

    public static class Int_T extends MyType {
        @Override
        public String toString() {
            return "Int_T";
        }
    }

    public static class Bool_T extends MyType {
        @Override
        public String toString() {
            return "Bool_T";
        }
    }

    public static class Unit_T extends MyType {
        @Override
        public String toString() {
            return "Unit_T";
        }
    }

    public static class IntStackArray_T extends MyType {
        private final int size;

        public IntStackArray_T(int size) {
            this.size = size;
        }

        public int getSize() {
            return size;
        }

        @Override
        public String toString() {
            return "IntStackArray_T[" + size + "]";
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

        @Override
        public String toString() {
            return "CharStackArray_T[" + size + "]";
        }
    }

    public static class IntHeapArray_T extends MyType {
        @Override
        public String toString() {
            return "IntHeapArray_T";
        }
    }

    public static class CharHeapArray_T extends MyType {
        @Override
        public String toString() {
            return "CharHeapArray_T";
        }
    }

    public static class StringArray_T extends MyType {
        @Override
        public String toString() {
            return "StringArray_T";
        }
    }
}
