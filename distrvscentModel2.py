import matplotlib.pyplot as plt

dist_index = ['1', '2', '4', '8', '16', '32']
dist_times = [697.176, 173.63, 43.36, 10.870, 2.679, 0.6302]

plt.bar(dist_index, dist_times, color=['m','c','c','c','c','c'], width=0.3)
plt.xlabel('Hospitals')
plt.ylabel('Computation time (mins)', )

plt.suptitle("Centralized vs. Distributed Model Computation time \n (total n = 1000)", )

#plt.show()
plt.savefig('DistCenCompareV.pdf', format='pdf')


