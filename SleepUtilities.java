public class SleepUtilities {
    public static void nap() throws InterruptedException {
        nap(NAP_TIME);          // sleep between zero and NAP_TIME s
    }

    public static void nap(int duration) throws InterruptedException {
        int sleeptime = (int) (NAP_TIME * Math.random() );
        try { Thread.sleep(sleeptime * 1000); }
        catch (InterruptedException e) { throw e; }
    }
    private static final int NAP_TIME = 5;
}
