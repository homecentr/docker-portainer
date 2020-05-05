#!/usr/bin/with-contenv ash

START_TIME=`date +%s`
WAIT_TIMEOUT=${WAIT_FOR_NS_RECORD_TIMEOUT:-20}

if [ "$WAIT_FOR_NS_RECORD" == "" ]
then
  echo "WAIT_FOR_NS_RECORD is empty, skipping..."
  exit 0
fi

until nslookup "$WAIT_FOR_NS_RECORD" > /dev/null
do
  sleep 5

  ELAPSED_TIME=`expr \`date +%s\` - $START_TIME`

  if [ $ELAPSED_TIME -gt $WAIT_TIMEOUT ]
  then
    echo "Resolution of the NS record $WAIT_FOR_NS_RECORD has not been successful in the given timeout, failing the container..."
    exit 20
  else
    REMAINING_TIME=`expr $WAIT_TIMEOUT - $ELAPSED_TIME`
    echo "Resolution of the NS record $WAIT_FOR_NS_RECORD has failed, will keep retrying for $REMAINING_TIME seconds..."
  fi
done

echo "the NS record $WAIT_FOR_NS_RECORD resolved successfully"
exit 0