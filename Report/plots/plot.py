#!/usr/bin/env python
# coding: utf-8
import csv
import glob
import re
from matplotlib.pyplot import *

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

def load_file(name):
    with open(name) as f:
        reader = csv.reader(f)
        working_set = list(reader)

        no_comments = []
        for i in working_set:
            if i[0].startswith("#"):
                continue
            i = [int(i[0]), float(i[1])]
            no_comments.append(i)

        return no_comments

#plot_files(glob.glob('gossim*push-sum*.csv'), False)
#csvdata = load_file("fbapi-stats-1448918360248.csv")
#csvdata = load_file("2000users/fbapi-stats-1448926507445.csv")
files = glob.glob("*users/*csv")
colors = ['red', 'green', 'blue', 'orange', 'magenta', 'black', 'cyan']

#hold(True)

numNodes = 4

for i,f in enumerate(files):
    hold(False)
    hold(True)
    hold(False)

    numUsers = re.match(r'[0-9]+', f).group(0)
    numUsers = int(numUsers)*numNodes
    csvdata = load_file(f)

    times = [a[0] for a in csvdata]
    times = map(lambda x: float(x)/1000.0, times)
    minTime = min(times)
    maxTime = max(max(times), maxTime)
    values = [a[1] for a in csvdata]
    maxRps = max(values)

    j = 0
    for j,v in enumerate(values):
        if v == maxRps:
            break

    hold(True)
    plot(times, values, color=colors[i])
    plot(times[j], maxRps, marker='o', color='r', ls='')
    ha = "right"

    if times[j] < 10:
        ha = "left"

    text(times[j], maxRps, str(maxRps) + " r/s", va='bottom', ha=ha)
    axis((0.0, maxTime, 0.0, maxRps + maxRps*0.10))
    title(str(numUsers) + " Users Simulation")
    xlabel('Time (seconds)')
    ylabel('Requests per Second')

    #legend()
    #show()
    outName = "rps-%d.png" % numUsers

    savefig(outName)
    clf()
