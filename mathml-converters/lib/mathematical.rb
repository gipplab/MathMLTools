require 'mathematical'

input_array = ARGV
latex_input = "\$" + input_array[0] + "\$"

options	 = {:format => :mathml}
renderer = Mathematical.new(options)
out      = renderer.render(latex_input)
puts out[:data]
