public class test {

    public static void main(String[] args) {

        // TODO Auto-generated method stub
        long a = Long.MAX_VALUE;
        a++;
        System.out.println(a);
        a++;
        a++;
        System.out.println(a);
        System.out.println(Long.MAX_VALUE);
        System.out.println(a - (Long.MAX_VALUE + 1) + 1);
    }

}
