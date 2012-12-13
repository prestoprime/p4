declare namespace mets="http://www.loc.gov/METS/";
declare variable $COLLECTION as xs:string external;
for $mets in collection($COLLECTION)//mets:mets
where $mets[not(. //mets:file[@MIMETYPE='video/x-raw-yuv' and @MIMETYPE='audio/x-raw-int'])]
return data($mets/@OBJID)