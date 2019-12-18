package Analysis;

public abstract class MyType {

    public abstract int getSizeInBytes();

    public static class Char_T extends MyType {
        @Override
        public String toString() {
            return "Char_T";
        }

        public int getSizeInBytes() {
            return 1;
        }
    }

    public static class Int_T extends MyType {
        @Override
        public String toString() {
            return "Int_T";
        }

        public int getSizeInBytes() {
            return 4;
        }
    }

    public static class Bool_T extends MyType {
        @Override
        public String toString() {
            return "Bool_T";
        }

        public int getSizeInBytes() {
            return 1;
        }
    }

    public static class Unit_T extends MyType {
        @Override
        public String toString() {
            return "Unit_T";
        }

        public int getSizeInBytes() {
            return -1;
        }
    }

    public static class IntStackArray_T extends MyType {
        private final int length;

        public IntStackArray_T(int length) {
            this.length = length;
        }

        public int getLength() {
            return length;
        }

        public int getSizeInBytes() {
            return length * 4;
        }

        @Override
        public String toString() {
            return "IntStackArray_T[" + length + "]";
        }
    }

    public static class CharStackArray_T extends MyType {
        private final int length;

        public CharStackArray_T(int length) {
            this.length = length;
        }

        public int getLength() {
            return length;
        }

        public int getSizeInBytes() {
            return length * 1;
        }

        @Override
        public String toString() {
            return "CharStackArray_T[" + length + "]";
        }
    }

    public static class IntHeapArray_T extends MyType {
        @Override
        public String toString() {
            return "IntHeapArray_T";
        }

        public int getSizeInBytes() {
            return 4;
        }
    }

    public static class CharHeapArray_T extends MyType {
        @Override
        public String toString() {
            return "CharHeapArray_T";
        }

        public int getSizeInBytes() {
            return 4;
        }
    }

    public static class StringArray_T extends MyType {
        @Override
        public String toString() {
            return "StringArray_T";
        }

        public int getSizeInBytes() {
            return 4;
        }
    }
}
