declare default element namespace "http://www.w3.org/1998/Math/MathML";
declare function local:qvarMap($x) {
 map {"x" : (data($x/*[2]/@xml:id))}
};
for $m in db2-fn:xmlcolumn("math.math_mathml") return
for $x in $m//*:apply
[*[1]/name() = 'csymbol' and *[1][./text() = 'superscript'] and *[3]/name() = 'cn' and *[3][./text() = '2']]
where
fn:count($x/*[1]/*) = 0
 and fn:count($x/*[3]/*) = 0
 and fn:count($x/*) = 3

return
data($m/*[1]/@alttext)