declare default element namespace "http://www.w3.org/1998/Math/MathML";
declare function local:qvarMap($x) {
 map {"x" : (data($x/*[2]/*[2]/*[2]/@xml:id),data($x/*[2]/*[3]/@xml-id),data($x/*[3]/@xml-id)),"y" : (data($x/*[2]/*[2]/*[3]/*[2]/@xml:id),data($x/*[2]/*[2]/*[3]/*[3]/@xml-id))}
};
for $m in db2-fn:xmlcolumn("math.math_mathml") return
for $x in $m//*:apply
[*[1]/name() = 'plus' and *[2]/name() = 'apply' and *[2][*[1]/name() = 'plus' and *[2]/name() = 'apply' and *[2][*[1]/name() = 'csymbol' and *[1][./text() = 'superscript'] and *[3]/name() = 'apply' and *[3][*[1]/name() = 'times']]]]
where
fn:count($x/*[2]/*[2]/*[1]/*) = 0
 and fn:count($x/*[2]/*[2]/*[3]/*) = 3
 and fn:count($x/*[2]/*[2]/*) = 3
 and fn:count($x/*[2]/*) = 3
 and fn:count($x/*) = 3
 and $x/*[2]/*[2]/*[2] = $x/*[2]/*[3] and $x/*[2]/*[2]/*[2] = $x/*[3]
 and $x/*[2]/*[2]/*[3]/*[2] = $x/*[2]/*[2]/*[3]/*[3]

return
data($m/*[1]/@alttext)