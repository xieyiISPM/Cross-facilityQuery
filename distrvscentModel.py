import matplotlib.pyplot as plt

# distribute model vs Centralized model. Total records = 1000
hospitals = ['Centralized', '2', '4', '8', '16', '32']
times = [697.176, 173.63, 43.36, 10.870, 2.679, 0.6302]
color = ['r', 'b', 'b', 'b', 'b', 'b']
plt.bar(hospitals, times, width=0.5, color=color, edgecolor='k')
plt.xlabel('Hospitals', fontsize=12)
plt.ylabel('Computation time (mins)', fontsize=12)
plt.title("Distributed Model vs Centralized Model Computation time \n (total records = 1000)")
#plt.show()
plt.savefig('DistCenCompare.pdf', format='pdf')


