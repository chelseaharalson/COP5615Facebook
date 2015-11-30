#!/usr/bin/env python
# coding: utf-8
import csv
import glob
from matplotlib.pyplot import *

colors = ['red', 'green', 'blue', 'orange']
maxTime = 0
topo = ""

def doPlot(data, color, zoom):
    global maxTime, topo

    algo = data[0][2]
    topo = data[0][1]
    nodes = data[0][0][2:]
    data = data[3:len(data)-1]

    times = [a[0] for a in data]
    times = map(lambda x: float(x)/1000.0, times)
    maxTime = max(max(times), maxTime)
    values = [a[2] for a in data]
    if zoom:
        axis((0.0, 0.5, 0.0, float(nodes)))
    else:
        axis((0.0, maxTime, 0.0, float(nodes)))

    plot(times, values, label=algo, color=color)

def plot_files(files, zoom):
    global topo

    hold(False)
    for i, name in enumerate(files):

        print "Processing " + name

        with open(name) as f:
            reader = csv.reader(f)
            working_set = list(reader)

        doPlot(working_set, colors[i], zoom)
        hold(True)

    topo = str.upper(topo[0]) + topo[1:]
    title(topo + ' Topology Convergence')
    xlabel('Time (seconds)')
    ylabel('Converged Nodes')
    legend()
    outName = topo + '-convergence'

    if zoom:
        outName += '-zoom.png'
    else:
        outName += '.png'

    print "Saving as " + outName
    savefig(outName)
    #show()

plot_files(glob.glob('gossim*push-sum*.csv'), False)
plot_files(glob.glob('gossim*push-sum*.csv'), True)
plot_files(glob.glob('gossim*gossip*.csv'), False)
plot_files(glob.glob('gossim*gossip*.csv'), True)
