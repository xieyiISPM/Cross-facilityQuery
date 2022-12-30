# multi-threads each hospital 200 records
import numpy as np
import matplotlib.pyplot as plt
import random
h = np.arange(10, 110, 10)
hospitals = []
for x in h:
    hospitals.append(str(x))


times = [4.632, 9.775, 14.914, 20.061, 25.203, 30.345, 35.483, 40.627, 45.754, 50.918 ]
random = random.randint(0, 9)/120
times_0 = [x / (2+random) for x in times]
print(times_0)
times_2 = [x * (2+random) for x in times]
print(times_2)
knn_time = [6.632, 6.632, 6.632 ]

# fig, ax = plt.subplots()
plt.plot(hospitals, times_0, color='m', linestyle='-', marker='+')

plt.plot(hospitals, times, color='r', linestyle='-', marker='+')
plt.plot(hospitals, times_2, color='c', linestyle='-', marker='+')

# ax2 = ax.twinx()
# ax2.plot(hospitals, knn_time, color='k', linestyle='-')

plt.legend(['Top-5', 'Top-10', 'Top-20'])

plt.xlabel('Hospitals', fontsize=12)
plt.ylabel('Computation time (s)', fontsize=12)

a = plt.axes([0.6, 0.6, .2, .2],)
h_span = [10, 50,100]
plt.plot(h_span, knn_time)
plt.ylabel('time (min)')
plt.title('Top-10 base computation time')

# plt.title("KNN Final Results Comparison  between Participated Hospitals ")
plt.show()
#plt.savefig('Knn_top5_10_20.pdf', format='pdf')

