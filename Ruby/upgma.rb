require 'narray'
require 'c:\Users\Tibor\RubymineProjects\BioInf\upgma_lib'

beginning = Time.now
#   input arguments
selectFile = ARGV[0]
selectMode = ARGV[1]
selectMethod = ARGV[2]
#   sequence file
txtfile = open(selectFile, 'r+')

beginning3 = Time.now
if selectMethod == "jc69"
  matrix = jukesCantor(txtfile, selectMode)
elsif selectMethod == "k80"
  matrix = kimura(txtfile, selectMode)
else
  abort("This method is not implemented")
end
puts "Sequence reading: #{(Time.now - beginning3).round(4)} sec"

nameList = createNamingList()

#   names the existing original nodes with lowercase letters from a to z
#   (goes on in the ASCII if more than 28 nodes)
nodesArray = []
for i in 0...matrix.shape[0]
  nodesArray.push(ElementaryNode.new(nameList[i]))
end

beginning2 = Time.now
while matrix.shape[0] > 2

  minimum = findMinimumValue(matrix)

  #   calculates dissimilarity values between the newly-constructed cluster and all other nodes
  #   as Dij = (Di+Dj)/2
  newClusterValues = []
  for i in 0...(matrix.shape[0])
    if i != minimum[1] and i != minimum[2]
      newClusterValues.push( (matrix[minimum[1], i] + matrix[minimum[2], i]) / 2)
    end
  end

  #   puts the new dissimilarity values of the created cluster where the values of
  #   one of the two cluster-forming members used to be in the matrix
  counter = 0
  for i in 0...(matrix.shape[0])
    if i != minimum[2] and i != minimum[1]
      matrix[minimum[2], i] = newClusterValues[counter]
      matrix[i, minimum[2]] = newClusterValues[counter]
      counter += 1
    end
  end

  #   construct the cluster name as a compound of former names, while making sure it's in alphabet order
  children = []
  if ((nodesArray[minimum[2]].instance_variable_get(:@name).ord) < (nodesArray[minimum[1]].instance_variable_get(:@name).ord))
    children.push(nodesArray[minimum[2]])
    children.push(nodesArray[minimum[1]])

  else
    children.push(nodesArray[minimum[1]])
    children.push(nodesArray[minimum[2]])
  end

  nodesArray[minimum[2]] = NewickNode.new(children, minimum[0])
  nodesArray.delete(nodesArray[minimum[1]])

  #   removes the row and column of the other member of the newly-formed cluster, thereby
  #   reducing the matrix size by one
  matrix = matrix.delete_at(nil, minimum[1])
  matrix = matrix.delete_at(minimum[1])

end
puts "UPGMA: #{(Time.now - beginning2).round(4)} sec"
lastNode = NewickNode.new(nodesArray,matrix[0,1])

file = open("newick.txt",'w+')
file.write(lastNode.instance_variable_get(:@name)+";")
file.close()

IO.popen("njplot.exe newick.txt")
puts "Total time: #{(Time.now - beginning).round(4)} sec"