import matplotlib.pyplot as plt
import numpy as np

# distribute model vs Centralized model. Total records = 1000
length_of_sequence_offline = np.arange(4600, 6000, 200)
length_of_sequence_online = np.arange(4600, 6000, 200)
#offline_time = [46,50,55,61,66,70,75,80,83,88,93,97,102,106,109,115,121,123,128,133]
offline_time = [106,109,115,121,123,128,133]

online_time = [1,2,3,4,6,7,8]
top_k = np.arange(100, 1100, 100)
top_k_time = [7.029965, 27.9680983333333, 62.7674483333333,111.563315,174.507998333333,250.929715,341.597731666667,446.532548333333,564.655365,697.176448333333]
top_k2_time = [5.967, 23.489, 50.08, 91.48, 144.84, 215.818 , 270.86, 375.142, 464.33, 599.57]


offline_plot = plt.subplot2grid((2, 2), (0, 0), )
online_plot = plt.subplot2grid((2, 2), (1, 0), )
top_k_plot = plt.subplot2grid((2, 2), (1, 1), )
top_k2_plot = plt.subplot2grid((2, 2), (0, 1),)

# offline plot

offline_plot.plot(length_of_sequence_offline, offline_time, color='k', linestyle='-', marker='+')
offline_plot.set_xlabel('Length of genomic sequence n', )
offline_plot.set_ylabel('Computation time (s)', )
offline_plot.set_title("SSF-offline")

# online plot

online_plot.plot(length_of_sequence_online, online_time, color='k', linestyle='-', marker='+')
online_plot.set_xlabel('Length of genomic sequence n',)
online_plot.set_ylabel('Computation time (ms)', )
online_plot.set_title("SSF-onine")


top_k_plot.plot(top_k, top_k_time, color='k', linestyle='-', marker='+')
top_k_plot.set_xlabel('Genomic records number m (n=1000)', )
top_k_plot.set_ylabel('Computation time (mins)',)
top_k_plot.set_title("SDCTkS")

top_k2_plot.plot(top_k, top_k2_time, color='k', linestyle='-', marker='+')
top_k2_plot.set_xlabel('Genomic records number m (n=500)', )
top_k2_plot.set_ylabel('Computation time (mins)',)
top_k2_plot.set_title("SDCTkS")

plt.subplots_adjust(left=0.1,
                    bottom=0.1,
                    right=0.95,
                    top=0.9,
                    wspace=0.4,
                    hspace=0.6)

#plt.show()
plt.savefig('Combined.pdf', format='pdf')