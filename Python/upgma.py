from upgma_lib import *
import os
import time

def main(distanceFunction):
    txtfile = open('sekvence1.txt', 'r+')
    start = time.time()
    matrix = distanceFunction(txtfile, "FASTA")
    end = time.time()
    print end - start
    start = time.time()
    nameList = createNameList()
    #returns an error if the number of different characters between two sequences is over 75% of all characters
    if matrix == "Error":
        print "Greska"
        return

    #else:
    #    print matrix

    #names the existing original nodes with lowercase letters from a to z
    # (goes on in the ASCII if more than 28 nodes)
    nodesArray = [ElementaryNode(nameList[i]) for i in range(len(matrix))]

    #print [node.name for node in nodesArray]

    while len(matrix) > 2:

        minimum = findMinimumValue(matrix)

        #calculates dissimilarity values between the newly-constructed cluster and all other nodes
        #as Dij = (Di+Dj)/2
        newClusterValues = []
        for i in range(len(matrix)):
            if i != minimum[1] and i != minimum[2]:
                newClusterValues.append((matrix[minimum[1]][i]+matrix[minimum[2]][i]) / 2)

        #puts the new dissimilarity values of the created cluster where the values of
        #one of the two cluster-forming members used to be in the matrix
        counter = 0
        for i in range(len(matrix)):
            if i != minimum[2] and i != minimum[1]:
                matrix[minimum[2]][i] = newClusterValues[counter]
                matrix[i][minimum[2]] = newClusterValues[counter]
                counter += 1

        #construct the cluster name as a compound of former names, while making sure it's in alphabet order
        children = []
        if ord(nodesArray[minimum[2]].name[0]) < ord(nodesArray[minimum[1]].name[0]):
            #newClusterLetter= nodesArray[minimum[2]]+nodesArray[minimum[1]]
            children.append(nodesArray[minimum[2]])
            children.append(nodesArray[minimum[1]])
        else:
            #newClusterLetter= nodesArray[minimum[1]]+nodesArray[minimum[2]]
            children.append(nodesArray[minimum[1]])
            children.append(nodesArray[minimum[2]])

        nodesArray[minimum[2]] = NewickNode(children, minimum[0])
        nodesArray.remove(nodesArray[minimum[1]])

        #removes the row and column of the other member of the newly-formed cluster, thereby
        #reducing the matrix size by one
        matrix = np.delete(matrix, minimum[1],0)
        matrix = np.delete(matrix, minimum[1],1)

        #print [node.name for node in nodesArray]
    end = time.time()
    print end - start
    return nodesArray, matrix


if __name__ == '__main__':
    start = time.time()
    array, matrix = main(kimura)

    lastNode = NewickNode(array, matrix[0][1])

    NewFile = open("newick.txt",'w+')

    NewFile.write(lastNode.name+";")

    NewFile.close()

    os.system("njplot newick.txt")

    end = time.time()
    print end - start