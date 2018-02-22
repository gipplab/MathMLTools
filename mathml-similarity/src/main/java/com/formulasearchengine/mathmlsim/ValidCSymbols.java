package com.formulasearchengine.mathmlsim;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * CSymbols have been taken from http://www.openmath.org/cdindex.html
 */
public class ValidCSymbols {
    private ValidCSymbols() { }

    private static final String[] validcSymbols = new String[] {"A", "above", "abs", "absolute_zero", "acceleration", "acre", "acre_us_survey", "action", "addition", "addition", "additive_group", "additive_group", "affine_coordinates", "algorithm", "alternate-representation", "alternating_group", "alternating_group", "alternatingn", "altitude", "ambient_ring", "amp", "and", "angle", "anonymous", "anonymous", "anti-Hermitian", "antisymmetric", "append", "apply_to_list", "approx", "arc", "arccos", "arccos", "arccosh", "arccosh", "arccot", "arccot", "arccoth", "arccoth", "arccsc", "arccsc", "arccsch", "arccsch", "arcsec", "arcsec", "arcsech", "arcsech", "arcsin", "arcsin", "arcsinh", "arcsinh", "arctan", "arctan", "arctan", "arctanh", "arctanh", "are_conjugate", "are_distinct", "are_on_circle", "are_on_conic", "are_on_line", "area", "argument", "arrowset", "assertion", "assignment", "associative", "asynchronousError", "atto", "attribution", "automorphism_group", "automorphism_group", "automorphism_group", "automorphism_group", "automorphism_group", "automorphism_group", "automorphism_group", "automorphism_group", "Avogadros_constant", "banded", "bar", "base", "based_float", "based_integer", "Bell", "below", "big_intersect", "big_union", "bigfloat", "bigfloatprec", "binder", "binomial", "block", "Boltzmann_constant", "Boolean", "both_sides", "bytearray", "C", "C", "calendar_month", "calendar_year", "call_arguments", "carrier", "carrier", "carrier", "carrier", "carrier", "carrier", "cartesian_power", "cartesian_product", "cartesian_product", "CD", "CDBase", "CDComment", "CDComment", "CDDate", "CDDefinition", "CDGroup", "CDGroupDescription", "CDGroupMember", "CDGroupName", "CDGroupURL", "CDGroupVersion", "CDName", "CDName", "CDReviewDate", "CDRevision", "CDSComment", "CDSignatures", "CDSReviewDate", "CDSStatus", "CDStatus", "CDURL", "CDURL", "CDUses", "CDVersion", "CDVersion", "ceiling", "center", "center", "center_of", "center_of_gravity", "centi", "centralizer", "characteristic_eqn", "charge", "circle", "class", "class", "class", "class", "classes", "CMP", "coefficient", "coefficient", "coefficient_ring", "coefficient_ring", "collect", "columncount", "completely_reduced", "completely_reduced", "complex_cartesian", "complex_cartesian_type", "complex_polar", "complex_polar_type", "concatenation", "concentration", "configuration", "conic", "conjugacy_class", "conjugacy_class_representative", "s", "conjugacy_classes", "conjugate", "conjugation", "conjugation", "cons", "const_node", "constant", "constant_type", "contentequiv", "convert", "conway_polynomial", "coordinates", "coordinatize", "corner", "cos", "cosh", "cot", "coth", "Coulomb", "csc", "csch", "curl", "current", "cycle", "cycle_type", "cycles", "cyclic_group", "cyclic_group", "cyclic_monoid", "cyclic_semigroup", "day", "deci", "decide", "def_arguments", "definitionURL", "defint", "degree", "degree", "degree_Celsius", "degree_Fahrenheit", "degree_Kelvin", "degree_wrt", "deka", "density", "depth", "derived_subgroup", "Description", "determinant", "diagonal_matrix", "diff", "difference", "digraph", "dihedral_group", "dihedral_group", "direct_power", "direct_power", "direct_power", "direct_power", "direct_product", "direct_product", "direct_product", "direct_product", "direct_product", "discrete_log", "discriminant", "displacement", "disprove", "distance", "divergence", "divide", "divide", "divides", "divides", "divisor_of", "DMP", "DMP", "DMPL", "DMPL", "domain", "domain", "domainofapplication", "e", "edgeset", "eigenvalue", "eigenvector", "elimination", "elimination", "emptyset", "emptyset", "emptyword", "encodingError", "endomap", "endomap_left_compose", "endomap_right_compose", "endpoint", "endpoints", "energy", "entry", "eq", "eqmod", "eqmod", "eqs", "equivalence", "equivalence_closure", "equivalent", "error", "euler", "evaluate", "evaluate", "evaluate_to_type", "exa", "Example", "exists", "exp", "expand", "expand", "expand", "explore", "expression", "expression", "expression", "expression", "expression", "extended_gcd", "extended_in", "factor", "factor", "factor_of", "factored", "factorial", "factorof", "factors", "factors", "false", "Faradays_constant", "femto", "Fibonacci", "field", "field_by_conway", "field_by_poly", "field_by_poly_map", "field_by_poly_vector", "find", "first", "fix", "float", "floor", "FMP", "fn_type", "foot", "foot_us_survey", "for", "forall", "force", "foreign", "foreign_attribute", "fraction_field", "free_field", "free_group", "free_magma", "free_monoid", "free_ring", "free_semigroup", "function", "function_block", "function_call", "function_definition", "gamma", "gas_constant", "gcd", "gcd", "gcd", "gcd", "generalized_quaternion_group", "generators", "geq", "GFp", "GFpn", "giga", "GL", "GLn", "global_var", "grad", "graded_lexicographic", "graded_lexicographic", "graded_reverse_lexicographic", "graded_reverse_lexicographic", "gramme", "graph", "gravitational_constant", "groebner", "groebner", "groebner_basis", "groebnered", "groebnered", "group", "group", "gt", "H", "halfline", "hecto", "Hermitian", "homomorphism_by_generators", "homomorphism_by_generators", "homomorphism_by_generators", "homomorphism_by_generators", "hour", "i", "ideal", "ideal", "identity", "identity", "identity", "identity", "identity", "identity", "if", "image", "imaginary", "implies", "in", "in", "in", "in", "in_radical", "incident", "indNat", "IndType", "infinity", "inp_node", "int", "int2flt", "integer", "integer_interval", "integer_type", "integers", "intersect", "intersect", "interval", "interval_cc", "interval_co", "interval_oc", "interval_oo", "inverse", "inverse", "inverse", "inverse", "inversion", "invertibles", "invertibles", "invertibles", "irreflexive", "is_affine", "is_associative", "is_automorphism", "is_automorphism", "is_automorphism", "is_automorphism", "is_automorphism", "is_automorphism", "is_automorphism", "is_bijective", "is_commutative", "is_commutative", "is_commutative", "is_commutative", "is_commutative", "is_commutative", "is_coordinatized", "is_domain", "is_endomap", "is_endomorphism", "is_endomorphism", "is_endomorphism", "is_endomorphism", "is_endomorphism", "is_endomorphism", "is_endomorphism", "is_equivalence", "is_field", "is_homomorphism", "is_homomorphism", "is_homomorphism", "is_homomorphism", "is_homomorphism", "is_homomorphism", "is_homomorphism", "is_ideal", "is_identity", "is_in", "is_invertible", "is_isomorphism", "is_isomorphism", "is_isomorphism", "is_isomorphism", "is_isomorphism", "is_isomorphism", "is_isomorphism", "is_list_perm", "is_maximal_ideal", "is_midpoint", "is_normal", "is_permutation", "is_prime_ideal", "is_primitive", "is_primitive", "is_primitive", "is_primitive_poly", "is_reflexive", "is_relation", "is_subfield", "is_subgroup", "is_subgroup", "is_submagma", "is_submonoid", "is_subring", "is_subsemigroup", "is_symmetric", "is_transitive", "is_transitive", "is_transitive", "is_zero_divisor", "isomorphic", "isomorphic", "isomorphic", "isomorphic", "isomorphic", "isomorphic", "isomorphic", "Joule", "k_subsets", "kernel", "kernel", "kilo", "lambda", "Lambda", "Laplacian", "LaTeX_encoding", "lcm", "lcm", "leading_coefficient", "leading_coefficient", "leading_monomial", "leading_term", "left_compose", "left_compose", "left_coset", "left_coset_representative", "left_cosets", "left_divides", "left_expression", "left_inverse", "left_multiplication", "left_multiplication", "left_multiplication", "left_multiplication", "left_multiplication", "left_quotient_map", "left_ref", "left_regular_representation", "left_regular_representation", "left_transversal", "length", "length", "length", "length", "leq", "lexicographic", "lexicographic", "lift_binary", "light_year", "limit", "limitation", "line", "list", "list_of_lengthn", "list_perm", "list_selector", "list_to_matrix", "list_to_poly_d", "list_to_vector", "list_type", "listendomap", "litre", "litre_pre1964", "ln", "ln", "local_var", "log", "log", "look_up", "Loschmidt_constant", "lower-Hessenberg", "lower-triangular", "lt", "m_poly_ring", "magma", "magma", "magnetic_constant", "make_Semigroup", "make_Setoid", "map", "map", "map_with_condition", "map_with_target", "map_with_target_and_conditio", "n", "maps_monoid", "maps_semigroup", "mapsto", "mapsto", "mass", "MathML_encoding", "matrix", "matrix", "matrix_ordering", "matrix_ordering", "matrix_ring", "matrix_selector", "matrix_tensor", "matrix_type", "matrixcolumn", "matrixrow", "max", "mean", "mean", "median", "mega", "metre", "metre_sqrd", "metres_per_second", "metres_per_second_sqrd", "micro", "midpoint", "mile", "mile_us_survey", "miles_per_hr", "miles_per_hr_sqrd", "milli", "min", "minimal_groebner_element", "minimal_polynomial", "minus", "minus", "minus", "minus", "minute", "mode", "modulo_relation", "modulo_relation", "mole", "moment", "moment", "momentum", "monoid", "monoid", "monte_carlo_eq", "multinomial", "multiplication", "multiplication", "multiplication", "multiplication", "multiplication", "multiplication", "multiplicative_group", "multiplicative_group", "multiplicative_monoid", "multiset", "N", "Name", "NaN", "nano", "nary", "nassoc", "negation", "neq", "neqmod", "neqmod", "Newton", "Newton_per_sqr_metre", "nil", "node_selector", "normal_closure", "normalizer", "not", "notin", "notin", "notprsubset", "notprsubset", "notsubset", "notsubset", "nthdiff", "null", "NumericalValue", "Object", "omtype", "one", "op_node", "or", "orbit", "orbit", "orbits", "ord", "order", "order", "order", "ordering", "ordering", "oriented_interval", "other", "otherwise", "outerproduct", "P", "Pair", "Pair", "PairProj1", "PairProj1", "PairProj2", "PairProj2", "parallel", "partial_equivalence", "partialdiff", "partialdiffdegree", "partially_factored", "Pascal", "permutation", "permutation", "permutationsn", "perpbisector", "perpendicular", "perpline", "peta", "pi", "pico", "piece", "piecewise", "pint", "pint_us_dry", "pint_us_liquid", "PiType", "Planck_constant", "plus", "plus", "plus", "plus", "plus", "point", "polarline", "poly_d_named_to_arith", "poly_d_to_arith", "poly_r_rep", "poly_ring", "poly_ring_d", "poly_ring_d", "poly_ring_d_named", "poly_ring_d_named", "poly_ring_SLP", "poly_u_rep", "polynomial_assertion", "polynomial_r", "polynomial_ring", "polynomial_ring_r", "polynomial_ring_u", "polynomial_SLP", "polynomial_u", "pound_force", "pound_mass", "power", "power", "power", "power", "power", "power", "power", "powerset", "pre_order", "predicate_on_list", "prefix", "pressure", "primitive_element", "principal_ideal", "procedure_block", "procedure_call", "procedure_definition", "product", "prog_body", "Prop", "prove", "prove_in_theory", "prsubset", "prsubset", "Q", "Q", "quaternion_group", "quaternion_group", "quaternions", "quotient", "quotient", "quotient", "quotient_by_poly_map", "quotient_group", "quotient_map", "quotient_ring", "QuotientField", "R", "R", "radius", "radius_of", "range", "rank", "rank", "rational", "rational_type", "real", "real_type", "reduce", "reduce", "reflexive", "reflexive_closure", "relation", "remainder", "remainder", "resistance", "response", "rest", "restriction", "resultant", "return", "return", "return_node", "reverse", "reverse_lexicographic", "reverse_lexicographic", "right_compose", "right_compose", "right_coset", "right_coset_representative", "right_cosets", "right_divides", "right_expression", "right_inverse", "right_inverse_multiplication", "right_multiplication", "right_multiplication", "right_multiplication", "right_multiplication", "right_multiplication", "right_quotient_map", "right_ref", "right_transversal", "ring", "Role", "root", "round", "rowcount", "scalar", "scalarproduct", "schreier_tree", "sdev", "sdev", "SDMP", "SDMP", "sec", "sech", "second", "second", "segment", "select", "semigroup", "semigroup", "Semigroup", "set", "set_affine_coordinates", "set_coordinates", "set_type", "setdiff", "setdiff", "SetNumericalValue", "Setoid", "SigmaType", "SigmaType", "sign", "Signature", "sin", "sinh", "size", "size", "size", "size", "skew-symmetric", "SL", "SLn", "slp_degree", "source", "specification", "speed", "speed_of_light", "squarefree", "squarefreed", "stabilizer", "stabilizer", "stabilizer_chain", "Stirling1", "Stirling2", "strict_order", "string", "strings", "structure", "style", "subfield", "subgroup", "submagma", "submonoid", "subring", "subsemigroup", "subset", "subset", "subtraction", "subtraction", "succ", "suchthat", "suchthat", "sum", "support", "support", "sylow_subgroup", "symmetric", "symmetric", "symmetric_closure", "symmetric_group", "symmetric_group", "symmetric_groupn", "symtype", "tan", "tangent", "tanh", "target", "temperature", "tera", "term", "term", "term", "term", "time", "times", "times", "times", "times", "times", "times", "transitive", "transitive_closure", "transpose", "tridiagonal", "true", "trunc", "Tuple", "type", "type", "type", "type", "type", "type", "type", "Type", "Type0", "typecoerce", "typecoerce", "typecoerce", "typecoerce", "unary_minus", "unexpected", "unexpected_symbol", "unhandled_symbol", "union", "union", "unit_prefix", "unsupported_CD", "unwind", "upper-Hessenberg", "upper-triangular", "variables", "variance", "variance", "vector", "vector", "vector_selector", "vector_tensor", "vector_type", "vectorproduct", "velocity", "vertexset", "vierer_group", "volt", "voltage", "volume", "Watt", "week", "weighted", "weighted", "weighted_degree", "weighted_degree", "while", "xor", "yard", "yard_us_survey", "yocto", "yotta", "Z", "Z", "zepto", "zero", "zero", "zero", "zero", "zero", "zero_Celsius", "zero_Fahrenheit", "zetta", "Zm", "Zm"};
    public static final Set<String> VALID_CSYMBOLS = new HashSet<>(Arrays.asList(validcSymbols));
}
