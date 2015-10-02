package queue;

class YourMonitor {
    private int nCounters;
    boolean[] freeCounters;
    int numFreeCounters;

    final int size = 100;
    int currentCustomer;
    int currentQueueNum;

    // Put your attributes here...
    YourMonitor(int n) {
        nCounters = n;
        freeCounters = new boolean[n];
        currentCustomer = 0;
        currentQueueNum = 0;

    }
    /**
    * Return the next queue number in the intervall 0...99.
    * There is never more than 100 customers waiting.
    */
    synchronized int customerArrived() {
        int queueNum = currentQueueNum;
        currentQueueNum = ++currentQueueNum % size;
        notifyAll();

        return queueNum;
    }
    /**
    * Register the clerk at counter id as free. Send a customer if any.
    */
    synchronized void clerkFree(int id) {
        if (!freeCounters[id]) {
            freeCounters[id] = true;
            numFreeCounters++;
            notifyAll();
        }
    }
    /**
    * Wait for there to be a free clerk and a waiting customer, then
    * return the cueue number of next customer to serve and the counter
    * number of the engaged clerk.
    */
    synchronized DispData getDisplayData() throws InterruptedException {
        while (currentQueueNum == currentCustomer || numFreeCountersn <= 0) wait();
        
        int firstFreeCounter = indexOfTrue(freeCounters);
        DispData display = new DispData();
        display.queueNumber = currentQueueNum;
        display.clerkID = firstFreeCounter;
        numFreeCounters--;
        currentQueueNum = ++currentQueueNum % size;

        return display;
    }

    int indexOfTrue(arr) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i]) {
                return i;
            }
        }
        return -1;
    }
}
