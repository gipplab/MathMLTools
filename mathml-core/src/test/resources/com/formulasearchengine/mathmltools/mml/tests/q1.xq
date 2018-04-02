declare default element namespace "http://www.w3.org/1998/Math/MathML";
<result>{
    let $m := .for $x in $m//*:ci
    [./text() = 'I']
    where
        fn:count($x/*) = 0

    return
        $x}
</result>
