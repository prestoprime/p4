xquery version "1.0";
declare namespace wf="http://www.prestoprime.eu/model/2012/wf";
declare variable $userID as xs:string external;
declare variable $qstatus as xs:string external;

for $wfStatus in //wf:wfStatus
let $sParam := $wfStatus/wf:params/wf:dParamString[@key='userID']/text()
let $status := $wfStatus/@status
where
matches($sParam, $userID)
and
contains(upper-case($status), upper-case($qstatus))

return concat(data($wfStatus/@id), "&#9;", data($wfStatus/@status), "&#9;", data($wfStatus/@workflow), "&#9;", data($wfStatus/@startup), "&#9;", data($wfStatus/@duration), "&#9;", data($wfStatus/@totalSteps), "&#9;", data($wfStatus/@lastCompletedStep), "&#9;", data($wfStatus/wf:timeTable/wf:taskReport[last()]/@service))