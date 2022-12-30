import matplotlib.pyplot as plt

# distribute model vs Centralized model. Total records = 1000
# hospitals = ['2', '4', '8', '16', '32', 'Centralized']
# times = [173.63, 43.36, 10.870, 2.679, 0.6302, 697.176]

cent_index = ['0', '1', '2']
cent_times = [0, 697.176, 0]

dist_index = ['1', '2', '4', '8', '16', '32']
dist_times = [697.176, 173.63, 43.36, 10.870, 2.679, 0.6302]
dist = plt.subplot2grid((1, 2), (0, 0), )
cent = plt.subplot2grid((1, 2), (0, 1), )
#
# plt.bar(hospitals, times,  color=['tab:blue', 'tab:blue', 'tab:blue', 'tab:blue', 'tab:blue', 'tab:red'], width=0.5)
# plt.xlabel("Hospitals")
# plt.ylabel('Computation cost (mins)',)
# plt.gca().invert_yaxis()
dist.bar(dist_index, dist_times, color='c', width=0.5)
dist.set_ylabel('Hospitals')
dist.set_xlabel('Computation time (mins)', )

# dist.invert_yaxis()
#
cent.bar(cent_index, cent_times, color='m', width=0.4)
cent.set_ylabel('Centralized')
cent.set_xlabel('Computation time (mins)', )
#cent.invert_yaxis()

plt.suptitle("Centralized vs. Distributed Model Computation time \n (total n = 1000)", )

plt.subplots_adjust(
                    bottom=0.1,
                    top=0.85,
                    wspace=0.5,
                    hspace=0.4)
plt.show()
#plt.savefig('DistCenCompareV.pdf', format='pdf')


