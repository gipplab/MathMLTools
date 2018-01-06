declare default element namespace "http://www.w3.org/1998/Math/MathML";
for $m in db2-fn:xmlcolumn("math.math_mathml") return
for $x in $m//*:ci
[./text() = 'E']
where
fn:count($x/*) = 0

return
data($m/*[1]/@alttext)