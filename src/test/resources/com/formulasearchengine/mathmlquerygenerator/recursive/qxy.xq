declare default element namespace "http://www.w3.org/1998/Math/MathML";
declare function local:qvarMap($x) {
 map {"x" : (data($x/*[2]/@xml:id)),"y" : (data($x/*[3]/@xml:id))}
};

declare function local:compareApply($rootApply, $depth, $x ) {
(for $child in $x/* return local:compareApply(
if (empty($rootApply) and $child/name() = "apply") then $child else $rootApply,
if (empty($rootApply) and $child/name() = "apply") then 0 else $depth+1, $child),
if ($x/name() = "apply"
 and $x[*[1]/name() = 'divide']
 and fn:count($x/*) = 3
 ) then
data($m/*[1]/@alttext)
else ()
)};

for $m in db2-fn:xmlcolumn("math.math_mathml") return
local:compareApply((), 0, $m)