declare namespace mets="http://www.loc.gov/METS/";
declare namespace dc="http://purl.org/dc/elements/1.1/";

declare variable $qtitle as xs:string external;
declare variable $qformat as xs:string external;
declare variable $qdescription as xs:string external;
declare variable $qidentifier as xs:string external;

for $mets in //mets:mets

for $record in $mets/mets:dmdSec/mets:mdWrap/mets:xmlData/dc:record

where some $title in $record/dc:title, $identifier in $record/dc:identifier, $format in $record/dc:format, $description in $record/dc:description
satisfies 
contains(upper-case($title),upper-case($qtitle)) and 
contains(upper-case($identifier),upper-case($qidentifier)) and 
contains(upper-case($format),upper-case($qformat)) and
contains(upper-case($description),upper-case($qdescription))

return data($mets/@OBJID)