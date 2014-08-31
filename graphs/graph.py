import matplotlib.pyplot as plt
import networkx as nx
fh=open("dummy.edges", 'rb')
G=nx.read_adjlist(fh)

nx.draw(G)
plt.show()
