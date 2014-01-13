require 'narray'

def pass

end

class ElementaryNode
  def initialize(name)
    @name = name
    @minimum = 0
  end
end

  class NewickNode
    def initialize(children, minimum)
      @children = children
      @minimum = minimum.to_f / 2
      output = "("
      output += @children[0].instance_variable_get(:@name)
      output += ":"+(@minimum-@children[0].instance_variable_get(:@minimum)).to_s
      output += ","
      output += @children[1].instance_variable_get(:@name)
      output += ":"+(@minimum-@children[1].instance_variable_get(:@minimum)).to_s
      output += ")"
      @name = output
    end
  end

  def createNamingList()
    list = []
    for i in 0...("Z".ord + 1 - "A".ord)
      list.push(("A".ord+i).chr)
    end
    for i in 0...("Z".ord + 1 - "A".ord)
      for j in 0...("Z".ord + 1 - "A".ord)
        list.push(("A".ord+i).chr + ("A".ord+j).chr)
      end
    end
    list
  end

  def findMinimumValue(matrix)

    #   Finds the minimum value in the lower triangulate of a square matrix,
    #   complexity O(nlog(n))
    #
    #   Input:
    #   matrix: narray matrix object
    #
    #   Output:
    #   ruby list object containing the minimum value and its indice

    min = matrix[0, 1]
    minI = 0
    minJ = 1
    for i in 0...(matrix.shape[0])
      for j in 0..(i)
        if (matrix[i, j] != 0 and matrix[i, j] < min)
          min = matrix[i, j]
          minI = i
          minJ = j
        end
      end
    end
    [min, minI, minJ]
  end

  def to_readable(matrix)

    # Prints matrix in more readable style

    i = 0
    matrix.each do |number|
      print number.to_s + " "
      i+= 1
      if i == matrix.shape[0]
        print "\n"
        i = 0
      end
    end
  end

  def jukesCantor(file, type)

    #   Jukes-Cantor algorithm
    #   Reads from an input file containing aligned sequences and creates a dissimilarity matrix.
    #
    #   Input:
    #   file: ruby file object
    #
    #   Output:
    #   matrix: narray matrix object

    sequences = []
    tmpSequence = ""
    if type == "FASTA"
      for line in file
        if line[0] == '>'
          if tmpSequence != ""
            sequences.push(tmpSequence)
            tmpSequence = ""
          end
        else
          tmpSequence += line.strip
        end
      end
      sequences.push(tmpSequence)
    else
      for line in file
        sequences.push(line.strip)
      end
    end

    matrix = NArray.float(sequences.length, sequences.length)

    i = 0
    j = 0

    sequences.each { |seq1|
      j = 0
      sequences.each { |seq2|
        if i > j
          j += 1
          next
        elsif i == j
          matrix[i, j] = 0
        else
          numdiff = 0
          for index in 0...(seq1.length)
            if seq1[index] != seq2[index]
              numdiff += 1
            end
          end
          if (numdiff == seq1.length) or ((numdiff.to_f/seq1.length) >= 0.75)
            return "Error"
          end
          numdiff = numdiff.to_f / seq1.length
          distance = -0.75 * Math.log((1 - (4.to_f / 3) * numdiff), Math::E)
          matrix[i, j] = distance
          matrix[j, i] = distance
        end
        j += 1
      }
      i += 1
#      print i
#      puts
    }
    matrix
  end

def kimura(file, type)

  #   Kimura algorithm
  #   Reads from an input file containing aligned sequences and creates a dissimilarity matrix.
  #
  #   Input:
  #   file: ruby file object
  #
  #   Output:
  #   matrix: narray matrix object

  sequences = []
  tmpSequence = ""
  if type == "FASTA"
    for line in file
      if line[0] == '>'
        if tmpSequence != ""
          sequences.push(tmpSequence)
          tmpSequence = ""
        end
      else
        tmpSequence += line.strip
      end
    end
    sequences.push(tmpSequence)
  else
    for line in file
      sequences.push(line.strip)
    end
  end

  matrix = NArray.float(sequences.length, sequences.length)

  i = 0
  j = 0

  sequences.each { |seq1|
    j = 0
    sequences.each { |seq2|
      if i > j
        j += 1
        next
      elsif i == j
        matrix[i, j] = 0
      else
        translation = 0
        transversion = 0
        for index in 0...(seq1.length)
          if seq1[index] != seq2[index]
            if (seq1[index] == 'A' and seq2[index] == 'G') or (seq1[index] == 'G' and seq2[index] == 'A') or (seq1[index] == 'T' and seq2[index] == 'C') or (seq1[index] == 'C' and seq2[index] == 'T')
              translation += 1
            elsif seq1[index] == '-' or seq2[index] == '-'
              pass
            else
              transversion += 1
            end
          end
        end
        translation = translation.to_f / seq1.length
        transversion = transversion.to_f / seq1.length
        distance = -0.5 * Math.log(1 - 2 * translation - transversion, Math::E) -0.25 * Math.log(1 - 2 * transversion, Math::E)
        matrix[i, j] = distance
        matrix[j, i] = distance
      end
      j += 1
    }
    i += 1
#    print i
#    puts
  }
  matrix
end