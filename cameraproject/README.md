#project

##how to run
1. choose your two favourite cameras (I like argus-5.student.lth.se on port 7005 and 
argus-3.student.lth.se on port 7003), ssh into those cameras (password=sigge) then run ./proxyserver PORTNUMBER (replace PORTNUMBER with either 7005 or 7003, you can use your own if you'd like)
2. make sure that the values called in Demo.java match the ones you've chosen in step 1
3. run Demo.java
4. check that getting images works by using the test client that should pop up immediately
5. close the test client and then click "connect" on the GUI presented
6. it's amazing, you love it, everything is awesome
7. close the window to shutdown everything, make sure you are connected to the cameras if you want to destroy those processes too
