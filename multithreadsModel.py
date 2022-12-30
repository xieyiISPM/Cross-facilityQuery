# multi-threads each hospital 200 records
import numpy as np
import matplotlib.pyplot as plt
h = np.arange(10, 110, 10)
hospitals = []
for x in h:
    hospitals.append(str(x))
times = [13.48, 28.345, 42.226, 55.985, 70.965, 88.140, 96.657, 109.983, 124.767, 144.536]
# times = [13.48, 28.345, 42.226, 55.985, 70.965]
# times_2 = [13.48, 28.345, 42.226, 55.985, 70.965]

color = ['b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b', 'b']
plt.plot(hospitals, times, color='k', linestyle='-', marker='+')
plt.xlabel('Hospitals', fontsize=12)
plt.ylabel('Computation time (mins)', fontsize=12)
plt.title("Distributed Model With joined hospitals Computation time \n (each hospital records = 200)")
plt.show()
#plt.savefig('Multi-threads.pdf', format='pdf')

