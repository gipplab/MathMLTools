import sys
import latex2mathml.converter

latex_input   = sys.argv[1]
mathml_output = latex2mathml.converter.convert(latex_input)
print mathml_output
