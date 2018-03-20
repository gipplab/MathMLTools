declare default element namespace "http://www.w3.org/1998/Math/MathML";
for $m in db2-fn:xmlcolumn("math.math_mathml") return
for $x in $m//*:apply
[*[1]/name() = 'sin' and *[2]/name() = 'ci' and *[2][./text() = 'x']]
where
fn:count($x/*[2]/*) = 0
 and fn:count($x/*) = 2

return
data($m/*[1]/@alttext)