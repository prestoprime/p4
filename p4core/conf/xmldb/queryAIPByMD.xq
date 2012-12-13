xquery version "1.0";
declare namespace mets="http://www.loc.gov/METS/";
declare variable $COLLECTION as xs:string external;
declare variable $SEC_TYPE as xs:string external;
declare variable $MD_LABEL as xs:string external;
declare variable $STATUS as xs:string external;
for $mets in collection($COLLECTION)//mets:mets
where $mets//element()[local-name() = $SEC_TYPE and (not(@STATUS) or string-length($STATUS)=0 or @STATUS = $STATUS)]/mets:mdRef[@LABEL = $MD_LABEL]
return data($mets/@OBJID)